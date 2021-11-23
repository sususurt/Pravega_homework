# Pravega Homework
> A simple chat room based on Pravega.

**_Homework requirements:_**
- (Basic)One to One Chat ✅
- (Option)Group Chat ✅
- (Option)File transfer ✅


## Installation

1. Running Pravega as the standalone mode.  
2. Using Intellij open this project.

## Usage example

### One to One Chat
![One to One.png](https://i.loli.net/2021/11/23/QM93fxhSv7diImD.png)


### Group Chat
![Group chat.png](https://i.loli.net/2021/11/23/LZy85FmrpCzPxuX.png)

### File transfer
#### Upload
Prefix:`upload@`  
When the input message start with the prefix,
(`upload@/Users/user/test.txt`)
the content after will be treated as a path to the file.
If the file is upload successfully, it will display
`(Yourname) has upload a file.`
#### Download
If you have seen that someone has uploaded a file,
you can enter `download` in the terminal to download that file.
The file received is named with `recevedFile` under the project root directory.  
### Quit
Enter `bye` in the terminal.


## Meta

Ruitong Su @ECE-Duke Kunshan University

