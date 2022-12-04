![cover](https://github.com/Ladsers/Passtable-Lib/raw/master/.github/readme/github_readme_cover.png)

**The Passtable project**:ㅤ[Android app](https://github.com/Ladsers/Passtable-Android)ㅤ|ㅤ[Windows app](https://github.com/Ladsers/Passtable-for-Windows)ㅤ|ㅤ[JVM app (Linux & macOS)](https://github.com/Ladsers/Passtable-JVM)ㅤ|ㅤ<ins>Library</ins>
</br></br>


## Passtable
Kotlin library containing functions for storing passwords and confidential data. The core of the Passtable project applications. Works with ".passtable" files.

Requires Kotlin 1.7.10. Built with Gradle 7.5.1.

```
git clone https://github.com/Ladsers/Passtable-Lib.git
```
#### How to use the library in projects?
1. Download "passtable-lib-22.11.0.jar" from releases;
2. Create a "libs" folder at the root of the project;
3. Place "passtable-lib-22.11.0.jar" in a "libs" folder;
4. Add this line to the "dependencies" block in "build.gradle.kts":
```
implementation(files("libs/passtable-lib-22.11.0.jar"))
```
5. Load Gradle changes.

*Use the latest version instead of 22.11.0!*

### Contains
🔸 **Encryption functions** </br>
Simple functions for encryption and decryption accepting only data and password as input. Based on [Bouncy Castle](https://www.bouncycastle.org/).

🔸 **DataTable class** </br>
Class that implements storing and processing of confidential data.

🔸 **Verifier** </br>
Functions that check the correctness of the name for Windows & Linux & macOS, the primary password for ASCII set, data filling.

🔸 **Updater for the Passtable project apps** </br>
Base class used to check for new versions from a file on GitHub.

### Contributing
Here are some ways you can contribute:
+ [Submit Issues](https://github.com/Ladsers/Passtable-Lib/issues/new/choose) in Passtable-Lib or another the Passtable project repository;
+ [Submit Pull requests](https://github.com/Ladsers/Passtable-Lib/pulls) with fixes and features;
+ [Share your Ideas](https://github.com/Ladsers/Passtable-Lib/discussions/categories/ideas) about application.

### License
The code in this repository is licensed under the [Apache License 2.0](https://github.com/Ladsers/Passtable-Lib/blob/master/LICENSE.md). The third party resources used are listed in [NOTICE.md](https://github.com/Ladsers/Passtable-Lib/blob/master/NOTICE.md).
</br></br>
The Passtable logo can be used in applications or articles only when the Passtable project is explicitly mentioned.
