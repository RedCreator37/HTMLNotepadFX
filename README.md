# HTMLNotepadFX ![][versionBadge] ![][licenseBadge]

HTMLNotepadFX is a simple JavaFX-based HTML editor app (JavaFX HTMLEditor wrapped in a nice window).

*This is the first program I put on GitHub.*
*I hope that you understand that some things aren't done perfectly.*
*Contributions are welcome :D*

## Features

A simple program for editing HTML pages:

- Apply basic HTML and CSS formatting, insert an image, hyperlinks...
- View, export and print the HTML page and its source code
- WYSIWYG formatting
- Some fancy extras (make the window semi-transparent...)

## Known Bugs

Things that don't work as expected (yet). These should get fixed soon.

- Printing on macOS (fails to open the print dialog for some reason)

## To Do

Stuff that could have added / been done better:

- A simple way to deploy HTML pages to a web server
- Improved UI (some progress has been made with "experimental UI" but it's still way from looking great)
- Use of resource bundles or something else that'll allow the UI to be translated to other languages easily

Early development versions are in the *dev-html* branch.

## Requirements

HTMLNotepadFX is built on Java 11 and JavaFX (actually open source version of both, i.e. OpenJDK and OpenJFX).

You'll have to manually install JavaFX since it's no longer bundled with the JDK. 

## Usage

As said before, you need OpenJDK 11 and [OpenJFX SDK 11][openJFX] (older versions may work but
haven't been tested). Compile the source code and compress
the output into a .jar then run

```bash
java --module-path="path_to_your_openjfx_sdk" \
    --add-modules=javafx.controls,javafx.base,javafx.fxml,javafx.graphics,javafx.web,javafx.swing \
    -jar your_jar_file.jar
```

This is a temporary solution until the new version of Java Packager arrives.

[licenseBadge]: https://img.shields.io/badge/license-MIT-brightgreen.svg
[versionBadge]: https://img.shields.io/badge/version-0.4-ee912e.svg
[openJFX]: https://openjfx.io/
