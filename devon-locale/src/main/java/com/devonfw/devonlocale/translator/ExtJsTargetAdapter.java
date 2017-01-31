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
 * This is implementation class for ExtJs.This class converts map<String,Node> to Ext js format.
 *
 * @author ssarmoka
 */
public class ExtJsTargetAdapter implements TranslationTarget {

  private StringBuilder prefix = new StringBuilder(Constant.PREFIX_EXTJS);

  private StringBuilder postfix = new StringBuilder(Constant.POSTFIX_EXTJS);

  private StringBuilder startJsStringBuilder = new StringBuilder();

  private StringBuilder endJsStringBuilder = new StringBuilder();

  private StringBuilder completeJsString = new StringBuilder();

  /**
   * {@inheritDoc}
   */
  @Override
  public void generateStream(Map<String, Node> root, OutputStream out) {

    System.out.println("Complete js String is -- " + this.prefix.append(createJsString(root)).append(this.postfix));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void generateFile(Map<String, Node> root, File out) {

    try {
      if (!out.exists()) {
        out.createNewFile();
      }
      FileWriter fw = new FileWriter(out.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(this.prefix.append(createJsString(root)).append(this.postfix).toString());
      bw.close();
    } catch (IOException e) {
      System.out.println("ERROR:: " + e.getMessage());

    }

  }

  /**
   * This methods create extjs output string.
   *
   * @param root
   * @return
   */
  public StringBuilder createJsString(Map<String, Node> root) {

    Node node;

    Map<String, Node> childMap;
    Set<String> keySet = root.keySet();
    for (String key : keySet) {
      node = root.get(key);
      if (node.getText() == null && node.getChildren().size() > 1) {
        childMap = node.getChildren();
        Set<String> siblingNodeSet = childMap.keySet();
        this.startJsStringBuilder.append(key + ": {").append(Constant.NEW_LINE_CHAR);
        this.endJsStringBuilder.append("}");
        int i = 0;
        for (String siblingName : siblingNodeSet) {

          if (i != 0) {
            this.startJsStringBuilder.append(",");
          }
          this.startJsStringBuilder.append(siblingName + ":\'" + childMap.get(siblingName).getText() + "\'");
          i++;
        }

        if (this.completeJsString.toString().isEmpty()) {
          this.completeJsString.append("{").append(this.startJsStringBuilder).append(this.endJsStringBuilder);
        } else {
          this.completeJsString.append(",").append(Constant.NEW_LINE_CHAR).append(this.startJsStringBuilder)
              .append(this.endJsStringBuilder);
        }
        this.startJsStringBuilder = new StringBuilder();
        this.endJsStringBuilder = new StringBuilder();
      } else if (node.getText() == null && node.getChildren().size() == 1) {
        childMap = node.getChildren();
        this.startJsStringBuilder.append(key + ": {").append(Constant.NEW_LINE_CHAR);
        this.endJsStringBuilder.append("}");
        createJsString(childMap);
      } else if (node.getText() != null) {
        this.startJsStringBuilder.append(key + " : " + "\'" + node.getText() + "\'");
        if (this.completeJsString.toString().isEmpty()) {
          this.completeJsString.append(this.startJsStringBuilder).append(this.endJsStringBuilder);
        } else {
          this.completeJsString.append(",").append(Constant.NEW_LINE_CHAR).append(this.startJsStringBuilder)
              .append(this.endJsStringBuilder);
        }
        this.startJsStringBuilder = new StringBuilder();
        this.endJsStringBuilder = new StringBuilder();
      }

    }

    return this.completeJsString;
  }

}
