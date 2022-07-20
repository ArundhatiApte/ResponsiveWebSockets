"use strict";

const extractOptionsFromArgsOfShellOrExit = function(argv) {
  if (argv.length !== 4) {
    logAndExit("использование: <порт> <множитель числа ответа на двоичн. запрос>");
  }
  return {
    port: extractIntFromStringOrExit(argv[2], "порт"),
    multiplerForBinaryResponse: extractIntFromStringOrExit(
      argv[3],
      "множитель числа ответа на двоичн. запрос"
    )
  };
};

const logAndExit = function() {
  console.warn.apply(console, arguments);
  process.exit(-1);
};

const extractIntFromStringOrExit = function(string, nameOfOption) {
  const int = parseInt(string);
  if (isNaN(int)) {
    logAndExit(nameOfOption, " является не числом");
  }
  return int;
};

export default extractOptionsFromArgsOfShellOrExit;
