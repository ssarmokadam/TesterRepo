package com.devonfw.devonlocale.translator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import com.devonfw.devonlocale.common.Constant;
import com.devonfw.devonlocale.common.Node;

/**
 * This is implementation class for ExtJs.This class converts map<String,Node> to Json format.
 *
 * @author ssarmoka
 */
public class JsonTargetAdapter implements TranslationTarget {

  private StringBuilder startJsonStringBuilder = new StringBuilder();

  private StringBuilder endJsonStringBuilder = new StringBuilder();

  private StringBuilder completeJsonString = new StringBuilder();

  /**
   * {@inheritDoc}
   */
  public void generateStream(Map<String, Node> root, OutputStream out) {

    System.out.println("Complete json String is -- " + createJsonString(root).append("}"));

  }

  /**
   * {@inheritDoc}
   */
  public void generateFile(Map<String, Node> root, File out) {

    try {
      if (!out.exists()) {
        out.createNewFile();
      }
      FileWriter fw = new FileWriter(out.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      // StringBuilder finalJsonString = createJsonString(root).append("}");
      // String[] formatOutput = finalJsonString.toString().split(",");
      // for (String outputString : formatOutput) {
      // bw.write(outputString);
      // bw.write(",");
      // bw.newLine();
      // }
      bw.write(createJsonString(root).append("}").toString());
      bw.close();
    } catch (IOException e) {

      System.out.println("ERROR:: " + e.getMessage());

    }
  }

  /**
   * This methods create json output string.
   *
   * @param root
   * @return
   */
  public StringBuilder createJsonString(Map<String, Node> root) {

    Node node;

    Map<String, Node> childMap;
    Set<String> keySet = root.keySet();
    for (String key : keySet) {
      node = root.get(key);
      if (node.getText() == null && node.getChildren().size() > 1) {
        childMap = node.getChildren();
        Set<String> siblingNodeSet = childMap.keySet();
        this.startJsonStringBuilder.append("\"" + key + "\": {").append(Constant.NEW_LINE_CHAR);
        this.endJsonStringBuilder.append("}");
        int i = 0;
        for (String siblingName : siblingNodeSet) {
          if (i != 0) {
            this.startJsonStringBuilder.append(",");
          }
          this.startJsonStringBuilder
              .append("\"" + siblingName + "\" : " + "\"" + childMap.get(siblingName).getText() + "\"");
          i++;
        }

        if (this.completeJsonString.toString().isEmpty()) {
          this.completeJsonString.append("{").append(this.startJsonStringBuilder).append(this.endJsonStringBuilder);
        } else {
          this.completeJsonString.append(",").append(Constant.NEW_LINE_CHAR).append(this.startJsonStringBuilder)
              .append(this.endJsonStringBuilder);
        }
        this.startJsonStringBuilder = new StringBuilder();
        this.endJsonStringBuilder = new StringBuilder();
      } else if (node.getText() == null && node.getChildren().size() == 1) {
        childMap = node.getChildren();
        this.startJsonStringBuilder.append("\"" + key + "\": {").append(Constant.NEW_LINE_CHAR);
        this.endJsonStringBuilder.append("}");
        createJsonString(childMap);
      } else if (node.getText() != null) {
        this.startJsonStringBuilder.append("\"" + key + "\" : " + "\"" + node.getText() + "\"");
        if (this.completeJsonString.toString().isEmpty()) {
          this.completeJsonString.append("{").append(this.startJsonStringBuilder).append(this.endJsonStringBuilder);
        } else {
          this.completeJsonString.append(",").append(Constant.NEW_LINE_CHAR).append(this.startJsonStringBuilder)
              .append(this.endJsonStringBuilder);
        }

        this.startJsonStringBuilder = new StringBuilder();
        this.endJsonStringBuilder = new StringBuilder();

      }

    }

    return this.completeJsonString;
  }

}
