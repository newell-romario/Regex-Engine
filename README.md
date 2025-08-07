Welcome to my implementation of regex engine in Java. This is a regex engine that uses back-tracking when performing a match. Since we use back-tracking
we are susceptible to the common pitfalls of a regex engines that use back-tracking as its main way of matching. Although it's a toy implementation
it supports a fairly large portion of the regex dialect. This engine supports character classes, back-references, alternation, repetition, lazy repetition and anchors.
We don't support look-around assertions. Why? Since this is only a simple implementation as a portfolio project we decided to leave out that support.

Additionally, we support submatching extraction. 

See the driver class for a demo of how to use.

Lastly, one can use https://regex101.com/ and compare the results from that site versus the results of our engine. In most cases it should be consistent with the Java flavour regex or Re2 flavour of regex results.

