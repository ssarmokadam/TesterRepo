package com.devonfw.devonlocale.translator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import com.devonfw.devonlocale.common.Node;

/**
 * TODO ssarmoka This type ...
 *
 * @author ssarmoka
 */
public class TranslationTargetImpl implements TranslationTarget {

  private StringBuilder startJsonStringBuilder = new StringBuilder();

  private StringBuilder endJsonStringBuilder = new StringBuilder();

  private StringBuilder completeJsonString = new StringBuilder();

  /**
   * {@inheritDoc}
   */
  public void generateStream(Map<String, Node> root, OutputStream out) {

    PrintStream out1;
    out1 = new PrintStream(System.out, true);
    out1.println("Complete json String is -- " + createJsonString(root).append("}"));

  }

  /**
   * {@inheritDoc}
   */
  public void generateFile(Map<String, Node> root, File out) {

    try {
      if (!out.exists()) {
        out.createNewFile();
      }

      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
      bw.write(createJsonString(root).append("}").toString());
      bw.close();
    } catch (IOException e) {

      System.out.println("ERROR:: " + e.getMessage());
    }
  }

  public StringBuilder createJsonString(Map<String, Node> root) {

    Node node;

    Map<String, Node> childMap;
    Set<String> keySet = root.keySet();
    for (String key : keySet) {
      node = root.get(key);
      if (node.getText() == null && node.getChildren().size() == 1) {
        childMap = node.getChildren();
        this.startJsonStringBuilder.append("\"" + key + "\": {");
        this.endJsonStringBuilder.append("}");
        createJsonString(childMap);
      } else if (node.getText() != null) {
        this.startJsonStringBuilder.append("\"" + key + "\" : " + "\"" + node.getText() + "\"");
        if (this.completeJsonString.toString().isEmpty()) {
          this.completeJsonString.append("{").append(this.startJsonStringBuilder).append(this.endJsonStringBuilder);
        } else {
          this.completeJsonString.append(",").append(this.startJsonStringBuilder).append(this.endJsonStringBuilder);
        }

        this.startJsonStringBuilder = new StringBuilder();
        this.endJsonStringBuilder = new StringBuilder();

      } else {
        System.out.println("Out of scope hence skipped");
      }

    }

    return this.completeJsonString;
  }

}
