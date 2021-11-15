package com.devdroid.sleepassistant.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by zhuang on 2017/7/12
 */

public class ShellUtils {
  private static final String COMMAND_SU = "su";
  private static final String COMMAND_SH = "sh";
  private static final String COMMAND_EXIT = "exit\n";
  private static final String COMMAND_LINE_END = "\n";

  public ShellUtils() {
    throw new AssertionError();
  }

  public static boolean checkRootPermission() {
    return execCommand("echo root", true, false).result == 0;
  }

  public static CommandResult execCommand(String command, boolean isRoot) {
    return execCommand(new String[]{command}, isRoot, true);
  }

  private static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
    return execCommand(new String[]{command}, isRoot, isNeedResultMsg);
  }

  private static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
    int result = -1;
    if (commands == null || commands.length == 0) {
      return new CommandResult(result, null, null);
    }

    Process process = null;
    BufferedReader successResult = null;
    BufferedReader errorResult = null;
    StringBuilder successMsg = null;
    StringBuilder errorMsg = null;

    DataOutputStream os = null;
    try {
      process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
      os = new DataOutputStream(process.getOutputStream());
      for (String command : commands) {
        if (command == null) {
          continue;
        }
        os.write(command.getBytes());
        os.writeBytes(COMMAND_LINE_END);
        os.flush();
      }
      os.writeBytes(COMMAND_EXIT);
      os.flush();

      result = process.waitFor();
      if (isNeedResultMsg) {
        successMsg = new StringBuilder();
        errorMsg = new StringBuilder();
        successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
        errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String s;
        while ((s = successResult.readLine()) != null) {
          successMsg.append(s);
        }
        while ((s = errorResult.readLine()) != null) {
          errorMsg.append(s);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (os != null) {
          os.close();
        }
        if (successResult != null) {
          successResult.close();
        }
        if (errorResult != null) {
          errorResult.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      if (process != null) {
        process.destroy();
      }
    }
    return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
        : errorMsg.toString());
  }

  public static class CommandResult {

    int result;
    String successMsg;
    String errorMsg;

    public CommandResult(int result) {
      this.result = result;
    }

    CommandResult(int result, String successMsg, String errorMsg) {
      this.result = result;
      this.successMsg = successMsg;
      this.errorMsg = errorMsg;
    }
  }
}
