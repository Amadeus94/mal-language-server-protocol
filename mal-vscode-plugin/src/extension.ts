// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import * as vscode from 'vscode';
import * as path from 'path';
import { LanguageClient, LanguageClientOptions, ServerOptions } from 'vscode-languageclient';
import findJava from './util/findJava';

// main launcher class
const main: string = 'StdioLauncher';

// this method is called when your extension is activated
// your extension is activated the very first time the command is executed
export function activate(context: vscode.ExtensionContext) {

	// Use the console to output diagnostic information (console.log) and errors (console.error)
	// This line of code will only be executed once when your extension is activated
	console.log('Extension "mal-plugin" is now active!');

	const javaPath = findJava();
	console.log(`Java Path: ${javaPath}`);
	if (javaPath) {
		// launcher path
		let classPath = path.join(__dirname, '..', 'launcher', 'launcher.jar');
		const args: string[] = ['-cp', classPath];

		// server options
		let serverOptions: ServerOptions = {
			command: javaPath,
			args: [...args, main],
			options: {}
		};

		// client options
		let clientOptions: LanguageClientOptions = {
			// Register the server for plain text documents
			documentSelector: [{ scheme: 'file', language: 'mal' }],
			synchronize: { fileEvents: vscode.workspace.createFileSystemWatcher('**/.clientrc') }
		};

		// The command has been defined in the package.json file
		// Now provide the implementation of the command with registerCommand
		// The commandId parameter must match the command field in package.json
		let disposable = new LanguageClient('mal', 'MAL Language Server', serverOptions, clientOptions).start();

		context.subscriptions.push(disposable);
	}else{
		vscode.window.showErrorMessage("Failed to start 'MAL language server', could not locate JDK");
	}
}

// this method is called when your extension is deactivated
export function deactivate() {
	console.log('Extension "mal-plugin" is now deactivated!');
}
