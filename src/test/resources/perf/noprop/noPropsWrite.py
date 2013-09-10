#! /usr/bin/env python
# -*- coding: utf-8 -*-

__author__ = 'bachmanm'

import matplotlib.pyplot as plt
import numpy as np

data = np.empty((4, 13, 22), int)
data[0, :, :] = np.loadtxt(open("noPropsPlainDatabaseWrite.txt", "rb"), delimiter=";", dtype=int)
data[1, :, :] = np.loadtxt(open("noPropsEmptyFrameworkWrite.txt", "rb"), delimiter=";", dtype=int)
data[2, :, :] = np.loadtxt(open("noPropsSimpleRelcountWrite.txt", "rb"), delimiter=";", dtype=int)
data[3, :, :] = np.loadtxt(open("noPropsFullRelcountWrite.txt", "rb"), delimiter=";", dtype=int)

means = np.mean(data[:, :, 2:], 2)

stddevs = np.std(data[:, :, 2:], 2)

xaxis = data[0, :, 1]

plt.plot(xaxis, means[0], c="purple", linewidth=2.0)
plt.plot(xaxis, means[1], c="red", linewidth=2.0)
plt.plot(xaxis, means[2], c="green", linewidth=2.0)
plt.plot(xaxis, means[3], c="blue", linewidth=2.0)
plt.xlabel('Number of Relationships per Transaction')
plt.ylabel('Time (microseconds)')
plt.title('Creating 10,000 Relationships with No Properties')
plt.legend(('Plain Database', 'Empty Framework', 'Simple Relcount', 'Full Relcount'), loc=3)
plt.yscale('log')
plt.xscale('log')
plt.errorbar(xaxis, means[0], yerr=stddevs[0])
plt.errorbar(xaxis, means[1], yerr=stddevs[1])
plt.errorbar(xaxis, means[2], yerr=stddevs[2])
plt.errorbar(xaxis, means[3], yerr=stddevs[3])
# plt.show()

# for LaTeX:

none = (1.0 / data[0, :, 2:]) / (1.0 / data[0, :, 2:])
empty = (1.0 / data[1, :, 2:]) / (1.0 / data[0, :, 2:])
simple = (1.0 / data[2, :, 2:]) / (1.0 / data[0, :, 2:])
full = (1.0 / data[3, :, 2:]) / (1.0 / data[0, :, 2:])

emptymean = np.mean(empty, 1)
simplemean = np.mean(simple, 1)
fullmean = np.mean(full, 1)

emptydev = np.std(empty, 1)
simpledev = np.std(simple, 1)
fulldev = np.std(full, 1)

s = ' Rels / Tx & Empty Framework & Simple Relcount & Full Relcount \\\\ \\hline \\hline \n'

for i in range(0, 13):
    s = s + "{0:.0f}".format(xaxis[i]) + \
        ' & $' + "{0:.0f}\%".format(emptymean[i] * 100) + ' \\pm ' "{0:.0f}\%".format(emptydev[i] * 100) + '$' + \
        ' & $' + "{0:.0f}\%".format(simplemean[i] * 100) + ' \\pm ' "{0:.0f}\%".format(simpledev[i] * 100) + '$' + \
        ' & $' + "{0:.0f}\%".format(fullmean[i] * 100) + ' \\pm ' "{0:.0f}\%".format(fulldev[i] * 100) + '$' + \
        ' \\\\ \\hline \n'

print s

s = ' Rels / Tx & No Framework & Empty Framework & Simple Relcount & Full Relcount \\\\ \\hline \\hline \n'

for i in range(0, 13):
    s = s + "{0:.0f}".format(xaxis[i]) + \
        ' & $' + "{0:.0f}".format(np.mean(data[0, i, 2:], 0) / 1000.0) + ' \\pm ' "{0:.0f}".format(
        np.std(data[0, i, 2:], 0) / 1000.0) + '$' + \
        ' & $' + "{0:.0f}".format(np.mean(data[1, i, 2:], 0) / 1000.0) + ' \\pm ' "{0:.0f}".format(
        np.std(data[1, i, 2:], 0) / 1000.0) + '$' + \
        ' & $' + "{0:.0f}".format(np.mean(data[2, i, 2:], 0) / 1000.0) + ' \\pm ' "{0:.0f}".format(
        np.std(data[2, i, 2:], 0) / 1000.0) + '$' + \
        ' & $' + "{0:.0f}".format(np.mean(data[3, i, 2:], 0) / 1000.0) + ' \\pm ' "{0:.0f}".format(
        np.std(data[3, i, 2:], 0) / 1000.0) + '$' + \
        ' \\\\ \\hline \n'

print s