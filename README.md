GraphAware Relationship Count Module
------------------------------------

[![Build Status](https://travis-ci.org/graphaware/neo4j-relcount.png)](https://travis-ci.org/graphaware/neo4j-relcount)

In some Neo4j applications, it is useful to know how many relationships of a given type, perhaps with different properties,
are present on a node. Naive on-demand relationship counting quickly becomes inefficient with large numbers of relationships
per node.

The aim of this [GraphAware](https://github.com/graphaware/neo4j-framework) Relationship Count Module is to provide an
easy-to-use, high-performance relationship counting mechanism.

Download
--------

Node: In order to use this module, you will also need the [GraphAware Framework](https://github.com/graphaware/neo4j-framework).

### Releases

Releases are synced to Maven Central repository. To use the latest release, [download it](http://search.maven.org/remotecontent?filepath=com/graphaware/neo4j-relcount/1.9-1.0/neo4j-relcount-1.9-1.0.jar)
and put it on your classpath. When using Maven, include the following snippet in your pom.xml:

    <dependencies>
        ...
        <dependency>
            <groupId>com.graphaware</groupId>
            <artifactId>neo4j-relcount</artifactId>
            <version>1.9-1.0</version>
        </dependency>
        ...
    </dependencies>

### Snapshots

To use the latest development version, just clone this repository, run `mvn clean install` and put the produced .jar
file (found in target) into your classpath. If using Maven for your own development, include the following snippet in
your pom.xml instead of copying the .jar:

    <dependencies>
        ...
        <dependency>
            <groupId>com.graphaware</groupId>
            <artifactId>neo4j-relcount</artifactId>
            <version>1.9-1.1-SNAPSHOT</version>
        </dependency>
        ...
    </dependencies>

### Note on Versioning Scheme

The version number has two parts, separated by a dash. The first part indicates compatibility with a Neo4j version.
 The second part is the version of the module. For example, version 1.9-1.2 is a 1.2 version of the module
 compatible with Neo4j 1.9.x.

### Compatibility

 This module is compatible with Neo4j v. 1.9.x and GraphAware Framework v. 1.9-1.3.

Usage
-----

Once set up (read below), it is very simple to use the API.

```java
    Node node = ... //find a node somewhere, perhaps in an index

    RelationshipCounter relationshipCounter = ... //instantiate some kind of relationship counter

    int count = relationshipCounter.count(node); //DONE!
```

A few different kinds of relationship counters are provided. There are two categories: [_simple_ relationship counters](#simple),
found in `com.graphaware.relcount.simple`, only deal with relationship types and directions, but ignore relationship
properties. [_Full_ relationship counters](#full), found in `com.graphaware.relcount.full`, on the other hand, are more powerful
as they take relationship properties into account as well.

<a name="simple"/>
Simple Relationship Counters
----------------------------

If the only thing of interest are relationship counts per type and direction (relationship properties don't matter),
it is best to use simple relationship counters, from simplicity and performance point of view.

### Simple Caching Relationship Counter

The most efficient simple counter is the `SimpleCachedRelationshipCounter`. As the name suggests, it counts relationships
by reading them from "cache", i.e. nodes' properties. In order for this caching mechanism to work, you need to be using
the [GraphAware Framework](https://github.com/graphaware/neo4j-framework) with `SimpleRelationshipCountModule` registered.

When using Neo4j in _embedded_ mode, the simplest default setup looks like this:

```java
    GraphDatabaseService database = ... //your database

    GraphAwareFramework framework = new GraphAwareFramework(database);
    framework.registerModule(new SimpleRelationshipCountModule());
    framework.start();
```

(For _server_mode, stay tuned, coming very soon!)

Now, let's say you have a very simple graph with 10 people and 2 cities. Each person lives in one of the cities (relationship
 type LIVES_IN), and each person follows every other person on Twitter (relationship type FOLLOWS).

In order to count all followers of a person named Tracy, who is represented by node with ID = 2 in Neo4j, you would write
  the following:

```java
    Node tracy = database.getNodeById(2);
    RelationshipCounter followers = new SimpleCachedRelationshipCounter(FOLLOWS, INCOMING);
    followers.count(tracy); //returns 9
```
For graphs with thousands (or more) relationships per node, this way of counting relationships can be an significantly
 faster than the "naive" approach of traversing all relationships.

### Simple Naive Relationship Counter

It is possible to use the `RelationshipCounter` API without any caching at all, using the "naive" approach.

The following snippet will count all Tracy's followers by traversing and inspecting all relationships:

```java
   Node tracy = database.getNodeById(2);

   RelationshipCounter followers = new SimpleNaiveRelationshipCounter(FOLLOWS, INCOMING);
   followers.count(tracy);
```

<a name="full"/>
Full Relationship Counters
--------------------------

Full relationship counters are capable of counting relationships based on their types, directions, and properties.

### Full Caching Relationship Counter

The most efficient full counter is the `FullCachedRelationshipCounter`. As the name suggests, it counts relationships by
reading them from "cache", i.e. nodes' properties. In order for this caching mechanism to work, you need to be using the
[GraphAware Framework](https://github.com/graphaware/neo4j-framework) with `FullRelationshipCountModule` registered.

When using Neo4j in _embedded_ mode, the simplest default setup looks like this:

```java
    GraphAwareFramework framework = new GraphAwareFramework(database);
    FullRelationshipCountModule module = new FullRelationshipCountModule();
    framework.registerModule(module);
    framework.start();
```

(For _server_mode, stay tuned, coming very soon!)

Now, let's say you have a very simple graph with 10 people and 2 cities. Each person lives in one of the cities (relationship
type LIVES_IN), and each person follows every other person on Twitter (relationship type FOLLOWS). Furthermore, there
can be an optional "strength" property on each FOLLOWS relationship indicating the strength of a person's interest into
the other person (1 or 2).

In order to count all followers of a person named Tracy, who is represented by node with ID = 2 in Neo4j, you would write
the following:

```java
    Node tracy = database.getNodeById(2);
    FullRelationshipCounter followers = module.cachedCounter(FOLLOWS, INCOMING);
    followers.count(tracy); //returns 9
```
Alternatively, if you don't have access to the module object from when you've set things up, you can instantiate the counter
directly:

```java
    Node tracy = database.getNodeById(2);
    FullRelationshipCounter followers = new FullCachedRelationshipCounter(FOLLOWS, INCOMING);
    followers.count(tracy); //returns 9
```

The first approach is preferred, however, because it simplifies things when using the module (or the Framework)
with custom configuration.

If you wanted to know, how many of those followers are very interested in Tracy (strength = 2):

```java
    Node tracy = database.getNodeById(2);
    FullRelationshipCounter followersStrength2 = module.cachedCounter(FOLLOWS, INCOMING).with(STRENGTH, 2);
    followersStrength2.count(tracy);
```

When counting using `module.cachedCounter(FOLLOWS, INCOMING)`, all incoming relationships of type FOLLOWS are taken into
account, including those with and without the strength property. What if, however, the lack of the strength property has
some meaning, i.e. if we want to consider "undefined" as a separate case? This kind of counting is referred to as "literal"
counting and would be done like this:

```java
    Node tracy = database.getNodeById(2);
    FullRelationshipCounter followers = module.cachedCounter(FOLLOWS, INCOMING);
    followers.countLiterally(tracy);
```

For graphs with thousands (or more) relationships per node, this way of counting relationships can be an order of
magnitude faster than a naive approach of traversing all relationships and inspecting their properties.

#### How does it work?

There is no magic. The module inspects all transactions before they are committed to the database and analyzes them for
any created, deleted, or modified relationships.

It caches the relationship counts as properties on each node, both for incoming and outgoing relationships. In order not
to pollute nodes with meaningless properties, a `RelationshipCountCompactor`, as the name suggests, compacts the cached
information.

Let's illustrate that on an example. Suppose that a node has no relationships to start with. When you create the first
outgoing relationship of type `FRIEND_OF` with properties `level` equal to `2` and `timestamp` equal to `1368206683579`,
the following property is automatically written to the node:

    _GA_FRC_FRIEND_OF#OUTGOING#level#2#timestamp#1368206683579 = 1

Let's break it down:
* `_GA_` is a prefix for all GraphAware internal metadata.
* `FRC_` is the default ID of the FullRelationshipCountModule. This can be configured on per-module basis and is useful
 for registering multiple modules performing the same functionality with different configurations.
* `FRIEND_OF` is the relationship type
* `#` is a configurable information delimiter GraphAware uses internally.
* `level` is the key of the first property
* `2` is the value of the first property (level)
* `timestamp` is the key of the second property
* `1368206683579` is the value of the second property (timestamp)
* `1` is the cached number of relationships matching this representation (stored as a value of the property)

*NOTE:* None of the application level nodes or relationships should have names, types, labels, property keys or values
containing the following Strings:
* `_GA_REL_`
* `_LITERAL_`
* `#` (can be changed if needed)

That includes user input written into properties of nodes and relationship. Please check for this in your application and
encode it somehow.

Right, at some point, after our node makes more friends, the situation will look something like this:

    _GA_FRC_FRIEND_OF#OUTGOING#level#2#timestamp#1368206683579 = 1
    _GA_FRC_FRIEND_OF#OUTGOING#level#1#timestamp#1368206668364 = 1
    _GA_FRC_FRIEND_OF#OUTGOING#level#2#timestamp#1368206623759 = 1
    _GA_FRC_FRIEND_OF#OUTGOING#level#2#timestamp#1368924528927 = 1
    _GA_FRC_FRIEND_OF#OUTGOING#level#0#timestamp#1368092348239 = 1
    _GA_FRC_FRIEND_OF#OUTGOING#level#2#timestamp#1368547772839 = 1
    _GA_FRC_FRIEND_OF#OUTGOING#level#1#timestamp#1368542321123 = 1
    _GA_FRC_FRIEND_OF#OUTGOING#level#2#timestamp#1368254232452 = 1
    _GA_FRC_FRIEND_OF#OUTGOING#level#1#timestamp#1368546532344 = 1
    _GA_FRC_FRIEND_OF#OUTGOING#level#0#timestamp#1363234542345 = 1
    _GA_FRC_FRIEND_OF#OUTGOING#level#0#timestamp#1363234555555 = 1

At that point, the compactor looks at the situation finds out there are too many cached relationship counts. More specifically,
there is a threshold called the _compaction threshold_ which by default is set to 20. Let's illustrate with 10.

The compactor thus tries to generalize the cached relationships. One such generalization might involve replacing the
 timestamp with a wildcard (_GA_*), generating representations like this:

    _GA_FRC_FRIEND_OF#OUTGOING#level#0#timestamp#_GA_*
    _GA_FRC_FRIEND_OF#OUTGOING#level#1#timestamp#_GA_*
    _GA_FRC_FRIEND_OF#OUTGOING#level#2#timestamp#_GA_*

Then it compacts the cached relationship counts that match these representations. In our example, it results in this:

     _GA_FRC_FRIEND_OF#OUTGOING#level#0#timestamp#_GA_* = 3
     _GA_FRC_FRIEND_OF#OUTGOING#level#1#timestamp#_GA_* = 3
     _GA_FRC_FRIEND_OF#OUTGOING#level#2#timestamp#_GA_* = 5

After that, timestamp will always be ignored for these relationships, so if the next created relationships is

    _GA_FRC_FRIEND_OF#OUTGOING#level#0#timestamp#1363266542345

it will result in

    _GA_FRC_FRIEND_OF#OUTGOING#level#0#timestamp#_GA_* = 4
    _GA_FRC_FRIEND_OF#OUTGOING#level#1#timestamp#_GA_* = 3
    _GA_FRC_FRIEND_OF#OUTGOING#level#2#timestamp#_GA_* = 5

The compaction process uses heuristics to determine, which property is the best one to generalize. In simple terms,
it is the most property with most frequently changing values (measured per relationship type).

That's how it works on a high level. Of course relationships with different levels of generality are supported
(for example, creating a `FRIEND_OF` relationship without a level will work just fine). When issuing a query
 like this

 ```java
    RelationshipCounter counter = new FullCachedRelationshipCounter(FRIEND_OF, OUTGOING);
    int count = counter.count(node);
 ```

on a node with the following cache counts

      _GA_FRC_FRIEND_OF#OUTGOING#level#3#timestamp#1368206683579 = 1
      _GA_FRC_FRIEND_OF#OUTGOING#level#2#timestamp#_GA_* = 10
      _GA_FRC_FRIEND_OF#OUTGOING#level#1#timestamp#_GA_* = 20
      _GA_FRC_FRIEND_OF#OUTGOING = 5 (no level or timestamp provided on these relationships)

the result will be... you guessed it... 36.

On the other hand, counting pure outgoing FRIEND_OF relationships with no properties would be done like this:

```java
    FullRelationshipCounter counter = new FullCachedRelationshipCounter(FRIEND_OF, OUTGOING);
    int count = counter.countLiterally(node);
```

and result in 5.

However, if you now issue the following query:
```java
    RelationshipCounter counter = module.cachedCounter(FRIEND_OF, OUTGOING)
        .with("level", 2)
        .with("timestamp", 123456789);

    int count = counter.count(node);
```
an `UnableToCountException` will be thrown, because the granularity needed for answering such query has been compacted
away. There are three ways to deal with this problem, either
* [configure the compaction threshold](#compaction) so that this doesn't happen, or
* [manually fallback to naive counting](#naive), using a `FullNaiveRelationshipCounter`, or
* [use `FullFallingBackRelationshipCounter`](#fallback), which falls back to naive counting approach automatically

### Advanced Usage

There are a number of things that can be tweaked here. Let's talk about the compaction threshold first.

<a name="compaction"/>
#### Compaction Threshold Level

What should the compaction threshold be set to? That depends entirely on the use-case. Let's use the people/places example
from earlier with FOLLOWS and LIVES_IN relationships. Each node will have a number of LIVES_IN relationships, but only
incoming (places) or outgoing (people). These relationships have no properties, so that's 1 property for each node.

Furthermore, each person will have incoming and outgoing FOLLOWS relationships with 3 possible "strengths": none, 1, and 2.
That's 6 more properties. A compaction threshold of 7 would, therefore be appropriate for this use-case.

If you know, however, that you are not going to be interested in the strength of the FOLLOWS relationships, you could well
set the threshold to 3. One for the LIVES_IN relationships, and 2 for incoming and outgoing FOLLOWS relationships.

The threshold can be set when constructing the module by passing in a custom configuration:

```java
    GraphAwareFramework framework = new GraphAwareFramework(database);

    //compaction threshold to 7
    RelationshipCountStrategies relationshipCountStrategies = RelationshipCountStrategiesImpl.defaultStrategies().with(7);
    FullRelationshipCountModule module = new FullRelationshipCountModule(relationshipCountStrategies);

    framework.registerModule(module);
    framework.start();
```

#### Relationship Weights

Let's say you would like each relationship to have a different "weight", i.e. some relationships should count for more
than one. This is entirely possible by implementing a custom `RelationshipWeighingStrategy`.

Building on the previous example, let's say you would like the FOLLOWS relationship with strength = 2 to count for 2
 relationships. The following code would achieve just that:

```java
   GraphAwareFramework framework = new GraphAwareFramework(database);

   RelationshipWeighingStrategy customWeighingStrategy = new RelationshipWeighingStrategy() {
       @Override
       public int getRelationshipWeight(Relationship relationship, Node pointOfView) {
           return (int) relationship.getProperty(STRENGTH, 1);
       }
   };

   RelationshipCountStrategies relationshipCountStrategies = RelationshipCountStrategiesImpl.defaultStrategies()
           .with(7) //threshold
           .with(customWeighingStrategy);

   FullRelationshipCountModule module = new FullRelationshipCountModule(relationshipCountStrategies);

   framework.registerModule(module);
   framework.start();
```

#### Excluding Relationships

To exclude certain relationships from the count caching process altogether, create a strategy that implements the
`RelationshipInclusionStrategy`. For example, if you're only interested in FOLLOWS relationship counts and nothing else,
you could configure the module as follows:

```java
    GraphAwareFramework framework = new GraphAwareFramework(database);

    RelationshipInclusionStrategy customRelationshipInclusionStrategy = new RelationshipInclusionStrategy() {
        @Override
        public boolean include(Relationship relationship) {
            return relationship.isType(FOLLOWS);
        }
    };

    RelationshipCountStrategies relationshipCountStrategies = RelationshipCountStrategiesImpl.defaultStrategies()
            .with(customRelationshipInclusionStrategy);

    FullRelationshipCountModule module = new FullRelationshipCountModule(relationshipCountStrategies);

    framework.registerModule(module);
    framework.start();
```

#### Excluding Relationship Properties

Whilst the compaction mechanism eventually excludes frequently changing properties anyway, it might be useful (at least
for performance reasons) to exclude them explicitly, if you know up front that these properties are not going to be used
in the counting process.

Let's say, for example, that each FOLLOWS relationship has a "timestamp" property that is pretty much unique for each
relationship. In that case, you might choose to ignore that property for the purposes of relationship count caching by
setting up the module in the following fashion:

```java
    GraphAwareFramework framework = new GraphAwareFramework(database);

    RelationshipPropertyInclusionStrategy customRelationshipPropertyInclusionStrategy = new RelationshipPropertyInclusionStrategy() {
        @Override
        public boolean include(String key, Relationship propertyContainer) {
            return !"timestamp".equals(key);
        }
    };

    RelationshipCountStrategies relationshipCountStrategies = RelationshipCountStrategiesImpl.defaultStrategies()
            .with(customRelationshipPropertyInclusionStrategy);

    FullRelationshipCountModule module = new FullRelationshipCountModule(relationshipCountStrategies);

    framework.registerModule(module);
    framework.start();
```

#### Deriving Relationship Properties

Sometimes, it might be useful to derive relationship properties that are not explicitly there for the purposes of
relationship counting. Let's say, for example, that you want to count the number of followers based on
the followers' gender. In that case, each FOLLOWS relationship could get a derived "followeeGender" property, value of
 which is the gender of the followed person. Such requirement would be achieved with the following setup:

```java
   GraphAwareFramework framework = new GraphAwareFramework(database);

   RelationshipPropertiesExtractionStrategy customPropertiesExtractionStrategy = new RelationshipPropertiesExtractionStrategy() {
       @Override
       public Map<String, String> extractProperties(Relationship relationship, Node pointOfView) {
           //all real properties
           Map<String, String> result = PropertyContainerUtils.propertiesToStringMap(relationship);

           //derived property from the "other" node participating in the relationship
           if (relationship.isType(FOLLOWS)) {
               result.put(GENDER, relationship.getOtherNode(pointOfView).getProperty(GENDER).toString());
           }

           return result;
       }
   };

   RelationshipCountStrategies relationshipCountStrategies = RelationshipCountStrategiesImpl.defaultStrategies()
           .with(IncludeAllNodeProperties.getInstance()) //no node properties included by default!
           .with(customPropertiesExtractionStrategy);

   FullRelationshipCountModule module = new FullRelationshipCountModule(relationshipCountStrategies);

   framework.registerModule(module);
   framework.start();
```

Counting would be done as usual:

```java
   Node tracy = database.getNodeById(2);

   RelationshipCounter maleFollowers = module.cachedCounter(FOLLOWS, INCOMING).with(GENDER, MALE);
   maleFollowers.count(tracy); //returns only male followers count

   RelationshipCounter femaleFollowers = module.cachedCounter(FOLLOWS, INCOMING).with(GENDER, FEMALE);
   femaleFollowers.count(tracy); //returns only female followers count
```

<a name="naive"/>
### Full Naive Relationship Counter

It is possible to use the `RelationshipCounter` API without any caching at all. You might want to fall back to the
naive approach of traversing through all relationships because you caught an `UnableToCountException`, or maybe you
simply don't have enough relationships in your system to justify the write-overhead of the caching approach.

It is still advisable to obtain your `RelationshipCounter` from a module, although the module might not need to be
registered with a running instance of the GraphAware framework. Even when using the naive approach, it is possible to
use custom strategies (`RelationshipWeighingStrategy`, `RelationshipPropertiesExtractionStrategy`, etc.) explained
above.

The following snippet will count all Tracy's followers by traversing and inspecting all relationships:

```java
   FullRelationshipCountModule module = new FullRelationshipCountModule();

   Node tracy = database.getNodeById(2);

   RelationshipCounter followers = module.naiveCounter(FOLLOWS, INCOMING);
   followers.count(tracy);
```

If you're using this just for naive counting (no fallback, no custom config), it is possible to achieve the same thing
using the following code, although the former approach is preferable.

```java
    Node tracy = database.getNodeById(2);
    RelationshipCounter following = new FullNaiveRelationshipCounter(FOLLOWS, OUTGOING);
    following.count(tracy);
```

<a name="fallback"/>
### Full Falling Back Relationship Counter

Although it is recommended to avoid getting `UnableToCountException`s by configuring things properly, there is an option
of an automatic fallback to the naive approach when the caching approach has failed, because the needed granularity for
counting some kind of relationship has been compacted away.

The following code snippet illustrates the usage:

```java
   GraphAwareFramework framework = new GraphAwareFramework(database);

   RelationshipCountStrategies relationshipCountStrategies = RelationshipCountStrategiesImpl.defaultStrategies().with(3);
   FullRelationshipCountModule module = new FullRelationshipCountModule(relationshipCountStrategies);

   framework.registerModule(module);
   framework.start();

   populateDatabase();

   Node tracy = database.getNodeById(2);

   RelationshipCounter followers = module.fallingBackCounter(FOLLOWS, INCOMING);
   assertEquals(9, followers.count(tracy));           //uses cache

   RelationshipCounter followersStrength2 = module.fallingBackCounter(FOLLOWS, INCOMING).with(STRENGTH, 2);
   assertEquals(3, followersStrength2.count(tracy));  //falls back to naive
```

### License

Copyright (c) 2013 GraphAware

GraphAware is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program.
If not, see <http://www.gnu.org/licenses/>.