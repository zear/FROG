F.R.O.G. - the Fantastic Rescue Of Greeny
=========================================

About
-----
F.R.O.G. is a 2D action-platformer game written in Java and using SDL 1.2, originally developed as a java class project.
It is currently aiming at the [GCW Zero] (http://www.gcw-zero.com) platform as its main target, although it is also known to run on GNU/Linux and Windows PCs.

Authors
-------
* Artur "zear" Rojek	- programming
* Daniel "Dnilo" Garcia	- graphics

License
-------
* source code	- [GNU Lesser General Public License, version 2.1] (https://www.gnu.org/licenses/lgpl-2.1.html) or later (LGPLv2.1+)
* graphics	- [Creative Commons Attribution-ShareAlike] (http://creativecommons.org/licenses/by-sa/4.0/) (CC BY-SA)

Status
------
The game is currently in development, with many of the features still missing.

Installation
------------

1. Requirements:
* Java compiler
* [SDLJava binding] (https://github.com/zear/sdljava)

2. Compilation:

The project Makefile provides an automatic way to build F.R.O.G.
In order to compile, type `make`.
After a successful compilation, running `make release` will produce a JAR file.
`make clean` provides a way to remove a compiled build together with a produced JAR file.

3. Running:
`LD_LIBRARY_PATH=":/path/to/libsdljava.so" java -jar frog-YYYY.MM.DD.jar`

The game is known to run at least with OpenJDK and JamVM JVMs.
