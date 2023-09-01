---
section: 32
x-masysma-name: scanning
title: Scanning Workflow and Tools
date: 2020/03/19 20:34:10
lang: en-US
author: ["Linux-Fan, Ma_Sys.ma (Ma_Sys.ma@web.de)"]
keywords: ["scanimgrename", "ma_scanner", "mdvl", "scanner", "ocr"]
x-masysma-version: 1.2.0
x-masysma-repository: https://github.com/m7a/lp-scanning
x-masysma-website: https://masysma.net/32/scanning.xhtml
x-masysma-owned: 1
x-masysma-copyright: |
  Copyright (c) 2020 Ma_Sys.ma.
  For further info send an e-mail to Ma_Sys.ma@web.de.
---
Background
==========

This document describes a workflow for scanning documents which is being used
at the Ma_Sys.ma. Additionally, the repository provides tools to aid with this
workflow.

Being very specific, it is expected that the scripts are not immediately useful
for general-purpose usage. However, they may still serve as an inspiration for
developing an own workflow.

Technical Details
=================

Scans are expected to be acquired in form of multiple PDF documents which
may potentially contain multiple pages (e.g. from the same scan run through an
automatic document feeder, short: ADF). Alternatively, scanning documents
one-by-one by using a locally connected scanner are supported.

For storage, scans are converted to PNG files with a resolution of 150 DPI and
8 colors. If the mode of scanning can be influenced, it is set to grayscale by
default.

As an additional feature, scanned pages can be processed by an OCR to retrieve
a six-digit number which is used for constucting the file name of form
`madocYYYYYY.png` where `YYYYYY` is the document's number.

Workflow
========

The Ma_Sys.ma scanning workflow starts with (often hand-written) documents that
are labelled by a _pagination stamp_ with consecutive six-digit numbers near the
begin of the document.

These documents are then processed by any of these two “ways”:

## Way A: Scanning one-by-one

 1. Script `ma_scanner2 -n` is invoked
 2. First document put into the scanner
 3. With [ENTER] the scan is triggered.
 4. After scanning, the next document is processed by putting it in the
    scanner and continuing from step 3. If no more documents remain, the
    scanning is completed by entering `q` followed by [ENTER]

In parallel, background processes convert the scan results to indexed grayscale
with 8 colors and OCR processes try to recognize the numbers and rename the
files accordingly

## Way B: Scanning with ADF

 1. A local FTP server is started.
 2. Using a networked-scanner with ADF, documents are scanned in multiple
    batches and uploaded to the FTP server in form of PDF documents.
 3. In the directory with the PDF files, `ma_scanner -n .` is invoked.
 4. Documents are converted and OCRed in parallel.

## After the scanning

After processing documents this way, two types of faults can be observed:

Wrongly named files
:   These can often be identified by being numbered outside the interval of
    scanned pages. For instance, if documents 002001 to 002300 were processed,
    files named `madoc992022.png` are likely to be wrongly named. One can
    identify these files by using a file manager's _sort by name_ function.
Unidentified files
:   If the OCR did not return any six-digit numbers for a file, it likely did
    not recognize the stamped number. In this case, the file will be called
    like its origin PDF + page number or `scan_YYY` with `YYY` being a
    consecutive numbering scheme.

In both cases, the wrongly named files need to be renamed. To do this, program
`scanimgrename` is invoked on all the files whose names are incorrect.
For instance, a typical invocation is `scanimgrename scan_???.png` to process
all the hand-scanned but unidentified files.

The `scanimgrename` tool
========================

## Name

`scanimgrename` -- rename scanned image files

## Synopsis

	Usage: scanimgrename [file...]

## Description

`scanimgrename` provides a minmalistic interface showing only the scanned
document and a field to enter a number which will automatically be prefixed by
a suitable number of zeroes if it is less than six digits. Upon pressing
[ENTER], the file is renamed and the next document is presented. Having
processed all files, the interface closes.

In case a file with the target file name already exists, pressing [ENTER] will
save the file name to a stack and turn to the already existing file under the
assumption that that file might have been mis-named. `scanimage` indicates this
mode by showing the number being entered in red as opposed to the regular blue
color. Once the rename conflict has been resolved, the color will turn blue
again.

## Configuration

In order to configure a different file name scheme, the source code
`ScanImgRename.java` nees to be changed and the `scanimgrename.jar` needs to be
rebuilt e.g. by invoking `ant jar`.

The `ma_scanner2` tool
======================

## Name

`ma_scanner2` -- Ma_Sys.ma Scanning Helper Script

## Synopsis

	Way A Usage ma_scanner2 [-n] [MODE RESOLUTION INDICES]
	Way B Usage ma_scanner2 [-n] DIRECTORY

## Description

Two different modes of invocation, corresponding to the different ways
explained for the workflow are available.

Way A
:   To perform scans of individual documents, the `scanimage` tool is used.
    `ma_scanner2` provides an interactive interface querying the user to press
    [ENTER] to perform the next scan. `MODE` can be one of Color, Gray or
    Lineart (default: Gray). `RESOLUTION` is the scanning resolution in DPI
    (default: 150) and `INDICES` is the number of colors to use for the output
    file. If `-1` is given, all the colors from the scanner are retained.
Way B
:   To process scanned documents from an ADF, all pages from PDF files in
    `DIRECTORY` are processed using parallel processes.

## Options

`-n`
:   If `-n` is given, _numbers_ are attempted to be assigned to the files by
    processing them through the Tesseract OCR.

## Examples

`ma_scanner2`
:   A plain invocation allows scanning documents without numbers.
`ma_scanner2 -n`
:   Scan individual pages and attempt to recognize the numbers.
`ma_scanner2 Color 300 -1`
:   Scan images without reducing colors and with elevated resolutions.
    This is useful for non-document scans.

## See also

Here are the links to the script's dependencies. Most of them are optional for
one of the ways described above, see their documentation to find out what they
are useful for:

[convert(1)](https://manpages.debian.org/buster/imagemagick-6.q16/convert-im6.q16.1.en.html)
[gimp(1)](https://manpages.debian.org/buster/gimp/gimp.1.en.html)
[parallel(1)](https://manpages.debian.org/buster/parallel/parallel.1.en.html)
[pdfimages(1)](https://manpages.debian.org/buster/poppler-utils/pdfimages.1.en.html)
[scanimage(1)](https://manpages.debian.org/buster/sane-utils/scanimage.1.en.html)
[tesseract(1)](https://manpages.debian.org/buster/tesseract-ocr/tesseract.1.en.html)
