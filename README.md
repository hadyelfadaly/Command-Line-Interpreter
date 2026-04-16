# Java Command-Line Interpreter

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Course](https://img.shields.io/badge/Course-Operating%20Systems-1E3A8A?style=flat-square)](#)
[![Status](https://img.shields.io/badge/Status-Completed-16A34A?style=flat-square)](#)

## Motivation and Project Overview

This project implements a lightweight command-line interpreter in Java as part of the Operating Systems course assignment.

The core objective is to simulate how a shell works internally:

- Parse user input into commands and arguments.
- Resolve relative and absolute paths against a tracked current working directory.
- Execute file-system and utility commands using Java I/O and NIO APIs.
- Support output redirection for selected commands.

The interpreter runs in an interactive loop and executes commands until the user exits.

## Core Engineering Features

1. Interactive shell loop that continuously reads and executes user commands.
2. Dedicated parser that separates command names from arguments.
3. Quoted argument handling using both single and double quotes.
4. Path resolution helper for robust relative/absolute path behavior.
5. Built-in output redirection support using `>` (overwrite) and `>>` (append) for:
   - `pwd`
   - `ls`
   - `cat`
   - `wc`
6. ZIP compression and extraction utilities, including recursive directory zip support.

## Supported Commands

| Command | Description | Example |
|---|---|---|
| `pwd` | Print current directory path | `pwd` |
| `cd` | Change current directory (`..` supported) | `cd ..` |
| `ls` | List directory contents (sorted) | `ls` |
| `mkdir` | Create one or more directories | `mkdir dir1 dir2` |
| `touch` | Create file or update last modified time | `touch notes.txt` |
| `rmdir` | Remove empty directory or all empty dirs with `*` | `rmdir temp` |
| `rm` | Remove a file | `rm old.txt` |
| `cat` | Print one file or concatenate two files | `cat a.txt b.txt` |
| `wc` | Print line/word/character count for a file | `wc report.txt` |
| `cp` | Copy a file to another path | `cp src.txt dst.txt` |
| `zip` | Create a zip archive | `zip archive.zip file1.txt file2.txt` |
| `zip -r` | Recursively zip a directory | `zip -r archive.zip MyFolder` |
| `unzip` | Extract zip archive (optional destination via `-d`) | `unzip archive.zip -d output` |
| `exit` | Terminate the interpreter | `exit` |

## Project Structure

```text
Assignment 1/
|-- Code/
|   |-- Main.java
|   |-- Parser.java
|   `-- Terminal.java
|-- Instructions.pdf
`-- README.md
```

## How to Run

### Prerequisites

- Java JDK 17+ installed
- Terminal opened at the assignment root folder

### 1. Compile

```bash
javac Code/*.java
```

### 2. Run

```bash
java Code.Main
```

### 3. Example Session

```text
> pwd
> mkdir testDir
> cd testDir
> touch "my notes.txt"
> ls
> wc "my notes.txt"
> cd ..
> zip -r backup.zip testDir
> unzip backup.zip -d restored
> exit
```

## Implementation Notes

- The parser stores the first token as the command name and remaining tokens as arguments.
- Quoted strings are merged into a single argument to support paths/names with spaces.
- The terminal tracks `currentDirectory` internally instead of relying on process-level directory changes.
- File operations are implemented using Java `File`, `Files`, and stream APIs.
- Zip extraction includes canonical-path validation to reduce unsafe path traversal entries.

## Future Enhancements

- Add command piping support (for example: `cat file.txt | wc`).
- Add `mv` command support for rename/move workflows.
- Add wildcard expansion for commands like `rm` and `cp`.
- Add unit tests for parser edge cases and command behavior.
- Improve error messages and command usage help output.

## Team Members

- Hady Hassan El Fadaly - [Github Profile](https://github.com/hadyelfadaly)
- Amr Karem - [Github Profile](https://github.com/amrkarem66)
- Marwan Sameh El Sayed - [Github Profile](https://github.com/MarwanMoafy11)
- Mahmoud Gonnah - [Github Profile](https://github.com/gonnah11)
- Mohamed Amr - [Github Profile](https://github.com/moharam1)
