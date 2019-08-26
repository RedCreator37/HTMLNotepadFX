# HTMLNotepadFX ![][licenseBadge] ![][versionBadge]

HTMLNotepadFX is a simple JavaFX-based HTML editor app (JavaFX HTMLEditor wrapped in a nice window).

*This is the first program I put on GitHub.*
*I hope that you understand that some things aren't done perfectly.*
*PRs are welcome :D*

## Features

A simple program to edit HTML pages:

- Apply HTML Text Formatting, insert an image, insert hyperlinks...
- View, export and print HTML source code
- WYSIWYG formatting
- Some fancy extras (make the window semi-transparent...)

## Known Bugs

Things that don't work as expected (yet). I'll try to fix them as soon as possible.

- Printing on macOS (it fails to open the print dialog for some reason)

## To Do

Features to include in later releases and things that should be improved:

- A simple way to deploy HTML pages to a server
- Improved UI (it currently looks pretty boring...)
- Make use of resource bundles or something else that'll allow the UI to be translated to other languages

## Requirements

HTMLNotepadFX is built on Java 11 and JavaFX (actually open source version of both, ie. OpenJDK and OpenJFX).

You'll have to manually install JavaFX since it's no longer bundled with JDK.
Also, if you'd like to make an executable you'll have to either do a "fat jar" (a jar with all dependencies included)
or convert this into a modular project.

I usually use a "fat jar" in combination with a simple script to load dependencies with the app itself.
This is a temporary solution until the new version of Java Packager arrives. 

[licenseBadge]: https://img.shields.io/badge/license-MIT-blue.svg
[versionBadge]: https://img.shields.io/badge/version-0.3-brightgreen.svg