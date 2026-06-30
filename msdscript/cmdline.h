/**
* Created by Jason Ablott on 1/13/25.
*/


#ifndef MSDSCRIPT_H
#define MSDSCRIPT_H

/**
 * enum to enable parsing and various user commands
 */
typedef enum {
    do_nothing,
    do_interp,
    do_print,
    do_pretty_print,
  } run_mode_t;

/**
* \brief uses commandline arguments
* \param argc the number of command line arguments passed to the program
* \param argv the array of characters passed in as the arguments
* \return void
*/
run_mode_t use_arguments(int argc, char *argv[]);

#endif //MSDSCRIPT_H
