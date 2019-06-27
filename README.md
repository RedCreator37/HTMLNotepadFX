# Notepad ![][licenseBadge] ![][versionBadge]

Notepad is a simple JavaFX-based HTML editor app (JavaFX HTMLEditor wrapped in a nice window).

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

Features to be included in later releases and things that should be improved:

- A simple way to deploy HTML pages to a web server
- Improved the UI (it currently looks really boring...)
- Make use of resource bundles or something else that'll allow the UI to be translated to other languages

## Requirements

Notepad is built on Java 11 and JavaFX (actually open source version of both, i.e. OpenJDK and OpenJFX).

You'll have to manually install JavaFX since it's no longer bundled with JDK. 

## Usage

As said before, you need OpenJDK 11 and [OpenJFX SDK 11][openJFX] (older versions may work but
aren't tested). Compile the source code and compress
it into a .jar then run

```bash
java --module-path="path_to_your_openjfx_sdk" \
    --add-modules=javafx.controls,javafx.base,javafx.fxml,javafx.graphics,javafx.web,javafx.swing,javafx.media \
    -jar your_jar_file.jar
```

This is a temporary solution until the new version of Java Packager arrives.

[licenseBadge]: https://img.shields.io/badge/license-MIT-blue.svg
[versionBadge]: https://img.shields.io/badge/version-0.3-brightgreen.svg
[openJFX]: https://openjfx.io/
