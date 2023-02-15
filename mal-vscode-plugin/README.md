# Mal Language Server Protocol

## Prerequisites

- Latest version of Node.js installed. (For running the client application)
- Java 11+ installed. (For building the language server)

## Instructions

1. Open the `/mal-vscode-plugin` directory as the root directory in Visual Studio Code.
2. Open the terminal and run `npm install`.
3. You should now be able to use the "Run & Debug" feature in Visual Studio Code or simply click `F5`.
4. Open the `file.mal` file located in the `/mal-vscode-plugin` directory. Opening files with .mal extension will start the language server.  

## Additional Information

- The uber-Jar containing the Language Server in the Client application is located in the `/launcher` directory.
- The example `file.mal` file is located in the `/mal-vscode-plugin` directory, and is taken from https://github.com/simonhacks repository

### Rebuilding the Language Server

If you want to rebuild the Language Server, follow these steps:

1. Build the Language Server in the `mal-ls/langserver` directory.
2. Build the `mal-ls/launcher` directory (which creates the uber jar).
3. Copy the created uber jar into the clients `/mal-vscode-plugin/launcher` directory.
3. Alternatively, you can use the PowerShell script located `/mal-vscode-plugin/launcher`-directory to automatically do the copy (if you are running windows).