/**
 * Author: Jason Ablott
 * 2/5/2025
 * CS 6013
 * Assignment 3 UNIX Shell
 */


#include <iostream>
#include <map>
#include "shelpers.h"
#include <unistd.h>
#include <sys/wait.h>


using namespace std;


void runChild(const Command &programToExec);
void runParent(Command programToExec);
void forkAndStart(Command const &programToStart);
void changeDirectory(const vector<const char*>& arguments);
void executeCd(const char* directory);
void signalHandler(int signal);
void closeUnusedFds(Command const &programToExec);


vector<Command> commands; // vector of commands passed from user via getline()
map<int, string> backgroundProcesses; // map of ints (pid's), and strings (execNames)  of background programs


/*
 * Main function handles getting user input and storing in commands, then calling cd or fork as needed
 */
int main() {
    // set up signal handler for background process updates
    signal(SIGCHLD, signalHandler);
    // on startup print whitespace and instruction line for user clarity
    cout << endl;
    cout << "Enter a program name and arguments to run, or type 'exit' to exit JShell" << endl;

    // read in arguments from command line until "exit", "control^d, or other signal, and handle them.
    while (true) {

        // setup and get commands from user
        cout << endl << "> "; // print new line and ">" prompt for readability
        string arguments; // initialize a string to put command line arguments into
        getline(cin, arguments); // get arguments from command-line via stdin
        cout << endl;// line for readability
        // handle empty command string
        if (strcmp(arguments.c_str(), "") == 0) {
            continue;
        }

        // check for background processes and notify user when command is entered
        if (!backgroundProcesses.empty()) {
            cout << endl << "Background programs currently running in JShell:" << endl;
            for (const auto& pair : backgroundProcesses) {
                cout << "->" << pair.second << endl; // print out any background process
            }
            cout << endl; // line for readability
        }

        // tokenize arguments using provided tokenize helper in shelpers.cpp
        const string &args = arguments;
        if (strcmp(args.c_str(), "exit") == 0) {exit(0);} // exit program if user enters "exit"
        vector<string> tokens = tokenize(args); // tokenize the string of arguments for use
        commands = getCommands(tokens); // make tokens into commands

        // use commands as directed to fork and run new child processes
        for (const Command& command : commands) {
            if (strcmp(command.execName.c_str(), "") == 0) {break;}
            // if user typed cd, run cd process
            if (strcmp(command.execName.c_str(), "cd") == 0 ) {
                changeDirectory(command.argv);
            // otherwise fork process into 2 to start executing commands
            } else {
                forkAndStart(command);
            }
        }
    } // end of loop getting commands
    return 0;
}

/*
 * Function to run child after fork executed, needs to handle pipes and redirection
 */
void runChild(const Command &programToExec) {

    // handle first command
    if (programToExec.inputFd == STDIN_FILENO && programToExec.outputFd != STDOUT_FILENO) {
        // dup output to stdout, handle error
        if (dup2(programToExec.outputFd, STDOUT_FILENO) == -1){
            perror("dup2 error");
            exit(1);
            }
        closeUnusedFds(programToExec); // close all fds except ours
    }

    // handle last command
    if (programToExec.outputFd == STDOUT_FILENO && programToExec.inputFd != STDIN_FILENO) {
        // dup input to stdin, handle error
        if (dup2(programToExec.inputFd, STDIN_FILENO) == -1) {
            perror("dup2 error");
            exit(1);
            }
        closeUnusedFds(programToExec); // close all fds except ours
    }

    // handle all other commands
    else if ((programToExec.inputFd != STDIN_FILENO) && (programToExec.outputFd != STDOUT_FILENO)) {
       // dup pipe ends to in/out, handle error
        if ((dup2(programToExec.outputFd, STDOUT_FILENO) == -1) ||
            (dup2(programToExec.inputFd, STDIN_FILENO) == -1)) {
            perror("dup2 error");
            exit(1);
            }
        closeUnusedFds(programToExec); // close all fds except ours
    }

    // EXEC
    // run exec to execute the new process, overwriting the parent process.
    const char* programFile = programToExec.argv[0]; // const char *file, the name of the program
    // Convert args to usable form to send via execvp to new process
    std::vector<char*> argumentsToSend; // make vector to store arguments to send to new process via exec
    for (const char* arg : programToExec.argv) {
        argumentsToSend.push_back(const_cast<char*>(arg)); // Use const_cast to convert to non-const
    }
    argumentsToSend.push_back(nullptr); // make sure last item is nullptr, required by execvp
    // now execvp to replace this shell instance with the new program
    if (execvp(programFile, argumentsToSend.data()) == -1) {
        perror("execvp failed");
        // close any open files descriptors + cleanup if error
        closeUnusedFds(programToExec);
        close(programToExec.inputFd);
        close(programToExec.outputFd);
        exit(1);
    }
}

/*
 * Function to run parent code after fork completes
 */
void runParent(const Command& programToExec, const int pid) {

    // close unused file descriptors
    if (programToExec.inputFd != STDIN_FILENO) {
        close(programToExec.inputFd);
    }
    if (programToExec.outputFd != STDOUT_FILENO) {
        close(programToExec.outputFd);
    }

    // check if the program should be sent to background and handle accordingly
    if (programToExec.background == true) {
        // add this program to background map
        cout << endl << "Adding this process to background: " << programToExec.execName << endl;
        backgroundProcesses[pid] = programToExec.execName;
        return; // return to shell instead of waiting
    }

    // wait for child process to finish if not background
    if (programToExec.background == false) {
        waitpid(pid, NULL, 0);
    }
}

/*
 * Function to handle fork() system call and separate child and parent processes after fork
 */
void forkAndStart(Command const &programToStart) {
    int const pid = fork();
    if (pid < 0){
        perror("fork failed"); // handle error when forking
        exit(1);
    }
    // separate processes
    if (pid == 0){
        runChild(programToStart);
    } else {
        runParent(programToStart, pid);
    }
}

/*
 * Main function to change directory, checks number/args and passes the correct info to executeCd()
 * to complete chdir() system call
 */
void changeDirectory(const vector<const char*>& arguments) {

    // handle empty command cd
    if (arguments.size() == 2) {
        executeCd(getenv("HOME"));
    }

    // handle command cd <dir>
    else if (arguments.size() == 3) {
        const char* directory = arguments[1];
        executeCd(directory);

    // handle bad input
    } else {
        perror("wrong number of arguments for cd command!");
    }
}

/*
 * Helper function to execute the chdir system call, check for errors, and print out result
 */
void executeCd(const char* directory) {
    if (directory == NULL) {
        perror("Directory is null!");
        return;
    }
    if (chdir(directory) == -1) {
        perror("chdir failed");
        return;
    }
    cout << endl << "Directory changed to: " << directory << endl;
}

/*
 * Function to handle SIGCHLD signal for removing background processes from list
 */
void signalHandler(int signal) {
    if (signal == SIGCHLD) {
        int pid = waitpid(-1, NULL, WNOHANG);
        if (pid == -1) {
            perror("waitpid failed");
        } else {
            // Check if the process ID exists in the map
            auto it = backgroundProcesses.find(pid);
            if (it != backgroundProcesses.end()) {
                // If process is found, print a message indicating completion
                cout << "Background process completed -> " << it->second << " (PID: " << pid << ")" << endl << endl << ">";
                cout.flush();
                // Remove background process from the map
                backgroundProcesses.erase(it);
            }
        }
    }
}

/*
 * Function to close all file descriptors other than STDIN, STDOUT, and this programs inputFd and outputFd
 */
void closeUnusedFds(Command const &programToExec) {
    string myCommand = programToExec.execName;
    for (Command command : commands) {
        // if not our command close duped fds
        if (strcmp(myCommand.c_str(), command.execName.c_str()) != 0) {
            if ((command.inputFd != STDIN_FILENO) && (command.inputFd != STDOUT_FILENO)) {
                close(command.inputFd);
            }
            if ((command.outputFd != STDOUT_FILENO) && (command.outputFd != STDIN_FILENO)) {
                close(command.outputFd);
            }
        }
    }
}