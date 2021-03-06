package com.devonfw.devcon.output;

import java.util.List;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.data.DevconOption;

/**
 * This defines the component which is responsible for all output generated by the application
 *
 * @author ivanderk
 */
public interface Output {

  void showMessage(String message, String... args);

  void showError(String message, String... args);

  void showGeneralHelp(String header, String usage, List<DevconOption> options, List<CommandModuleInfo> modules);

  void showCommandHelp(Command command);

  void showModuleHelp(CommandModuleInfo module);

  void status(String message, String... args);

  void statusInNewLine(String message, String... args);

  void success(String command);

}