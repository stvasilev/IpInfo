# IpInfo - a JavaFX IP info fetcher 

---
1. [About](#about)
2. [Features](#features)
    - Fetch
    - Save
3. [Prerequisites](#prerequisites)
4. [Usage](#usage)

--- 
## About
---
I wanted to make a GUI Java program, and discovered JavaFX, and noticed that https//ip-api.com/ offer free API for requests, so that's how it all happened.

The program displays the current machine's local IP and makes a default request to the API to display the public IP.

![Example](https://github.com/stvasilev/IpInfo/blob/master/example.jpg)

---
## Features
---

### Fetch
You can send requests to the API with an IP of your choice, it will give you feedback based upon what parameters you have checked.
If you have **not marked** any checkboxes or the IP field is **empty**, then such request is **invalid**.

### Save
The save button saves the current feedback onto a file in the directory of your choice in a `.txt` format.
An **empty** filename field or **closing** the directory chooser means the save request is **invalid**.

--- 
## Prerequisites
---

JDK|Version
---|-------
[LibericaJDK - Full](https://bell-sw.com/)| 14+
[OpenJDK](https://openjdk.java.net/) + [OpenJFX](https://openjfx.io/) | 14+

I recommend using **LibericaJDK**'s Full package, since it contains OpenJFX in it, hence less headaches to set it up.

---
## Usage
--- 
#### LibericaJDK
Set your Java `PATH` to the `bin` folder just run the `.jar` file via terminal with `java -jar ipInfo.jar`.

#### OpenJDK + OpenJFX
Set your java `PATH` to the `bin` folder and when yo will have to include the JavaFX lib path with `--module-path` and afterwards its modules with `--add-modules` as additional arguments arguments.
