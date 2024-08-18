# FileTreeGenerator

![SGCAM_18082024_183000749](https://github.com/user-attachments/assets/9202f090-666d-4781-a2e4-4b476c200a60)

Version required to run this application: `Java 22`

## What is This Application For?
This application allows an arbitrary number of files and directories to be generated recursively for file system testing purposes.

The application proposes to testers and developers the recursive creation of files and directories in an automatic and simple way, without having to create one directory and file at a time, which can be very tedious and end up diverting attention from what really matters: testing desired functionalities in the file system.

The functionality to generate files is not yet implemented. This first version of the project relies on generating only directories. The next version will also generate custom files.

## How Does the Application Work?
The application must be run using the following command in your terminal/cmd:

>`java -jar Generator.jar`

*Executing in any other way may not be possible, as the implementation depends on the existence of the JVM console (System.console)*

When opening the application, it will be requested that an existing path ending in a directory be provided where the main folder (which will contain all generated files and directories) will be stored.

Right after providing a path, the application asks for the name of the folder you want the program to create to store the arbitrarily generated copies of files and directories. This is necessary because, if there was no parent directory that stored everything, the entire file system would become a mess, with several files scattered throughout it! It is useful because, when you no longer need to use the test directory, you can delete it and all content within it will be deleted.

After defining the name of the parent directory, you will be asked what depth you want to generate the directories (the functionality will be explained below)

Then, the limit of files you would like to be generated will be requested (this mechanic will also be explained in more detail below) _not yet implemented_

Finally, a list of file extensions is requested, where each extension, separated by a comma, will serve to determine the type of each file created randomly (and this will also be explained later) _not yet implemented_

## Explaining the Logic and Mechanics of the Application
### File and Directory Generation Syntax
The syntax for generating files is as follows:
>`FileX-Y` -->  Where Filet is the name of the prototype file, X is the number of the current depth layer that this file is being generated and Y is the number of that generated file (if it is file 1, 2, 3...)

The syntax for generating directories follows the same pattern as that for files: 
>`DirX-Y` -->   Same logical explanation for the syntax of the generated files

### Directory Generation
Directories are generated using depth and extendability logic.

Let's say we want to generate 5 directories within a TestDir parent directory. 
The first level of TestDir will contain 5 generated directories.

Then, the value of 5 directories drops to 4, which means that 4 directories will be generated in the next level. The next level comprises each of the directories generated in the previous level - 1.
That is, if at our level 1 (within the TestDir directory) we have 5 child directories, at level 2 we must create 4 subdirectories in the first 4 directories generated recursively, with the fifth directory being empty (only with the random files generated, if the option has been configured) and so on, until the number of directories to be generated reaches 0, when no other subdirectories will be created, only files (if this option has been configured)

The generation of directories can be canceled by passing 0 to the application settings, only the parent directory being generated

### File Generation
Files are generated randomly.

When a limit number of files that can be generated is defined, we are defining a number that ranges from 0 to the maximum that the user has defined.

At each iteration level, the randomizer can create all files, some, or none (_more custom settings will be provided in the future_).

As long as directories exist, there will be the possibility of generating files.

File generation can be overridden by setting 0 to the setting in the application.
If at least one extension is not provided for the application, the files will not have a defined type.

### Extension List
The extensions provided by the user are processed and attached to the file names at the time of their creation using mathematical probability, where the chances of it being chosen to be part of a generated file will be calculated for each extension. _not yet implemented_

Like directories and files, which allow us to provide an arbitrary number for generation, extensions also allow an arbitrary number of extensions to be passed to the application. The probability of choosing a given extension decreases as the number of extensions increases.

Let's create a simple assumption where we provide 100 file extensions for generation. The algorithm defines that the choice rate for each extension is 1%. If we place 1000 extensions, the algorithm defines the choice rate of each extension as 0.1% and so on.

Extensions do not need to be in known formats such as .zip, .tar, .txt, .sh, .bin... You can pass any type of custom extension you want, as long as each extension provided has the correct format, where the extension is followed by . and each of them separated by a comma, so that it can be detected by the regular expression

## Example of generating directories
Let's use the following example, where we recursively generate 3 directories, 0 files and do not pass any type of extension, where the parent directory is called TestDir:

> Parent Dir: `TestDir`
 
> Recursive Dir Value: `3`
 
> File Threshold: `0`
 

```plaintext
Testdir/
├── Dir1-1/
│   ├── Dir2-1/
│   │   └── Dir3-1  ***EMPTY***
|   |
│   └── Dir2-2  ***EMPTY***
|
├── Dir1-2/
│   ├── Dir2-1/
│   │   └── Dir3-1  ***EMPTY***
|   |
│   └── Dir2-2   ***EMPTY***
|
└── Dir1-3  ***EMPTY***
```
The example was properly spaced so that it was perfectly readable.
If our example had an arbitrary file limit for generation, all folders (even empty ones) could or could not contain files.

## Final Considerations
Files are not generated infinitely, but rather as long as there are directories. _not yet implemented_

The number of files that will be generated is arbitrary, highly dependent on the probabilistic algorithm. _not yet implemented_

It would not be a good idea to generate a large number of directories, since they are generated using depth and extendability logic.

The graph of this logic can be read by imagining 2 axes: X and Y.
X grows from bottom to top, while Y grows from left to right.
X can be related to depth and Y can be related to extensibility (number of directories created at a given level).

**FILE GENERATION IS NOT YET AVAILABLE**

Note: Please use this application carefully and responsibly and always provide the correct data, to prevent the application from breaking and stopping running [EXCEPTIONS HAVE NOT YET BEEN HANDLED]
