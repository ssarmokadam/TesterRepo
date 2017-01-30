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

  private StringBuffer prefix = new StringBuffer(Constant.PREFIX_EXTJS);

  private StringBuffer postfix = new StringBuffer(Constant.POSTFIX_EXTJS);

  private StringBuffer startJsStringBuffer = new StringBuffer();

  private StringBuffer endJsStringBuffer = new StringBuffer();

  private StringBuffer completeJsString = new StringBuffer();

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
  public StringBuffer createJsString(Map<String, Node> root) {

    Node node;

    Map<String, Node> childMap;
    Set<String> keySet = root.keySet();
    for (String key : keySet) {
      node = root.get(key);
      if (node.getText() == null && node.getChildren().size() == 1) {
        childMap = node.getChildren();
        this.startJsStringBuffer.append(key + ": {");
        this.endJsStringBuffer.append("}");
        createJsString(childMap);
      } else if (node.getText() != null) {
        this.startJsStringBuffer.append(key + " : " + "\'" + node.getText() + "\'");
        if (this.completeJsString.toString().isEmpty()) {
          this.completeJsString.append(this.startJsStringBuffer).append(this.endJsStringBuffer);
        } else {
          this.completeJsString.append(",").append(this.startJsStringBuffer).append(this.endJsStringBuffer);
        }
        this.startJsStringBuffer = new StringBuffer();
        this.endJsStringBuffer = new StringBuffer();
      } else {
        System.out.println("ERROR:: This version of devon-locale supports only simple proprties. " + key
            + " This property will be ignored.");
      }

    }

    return this.completeJsString;
  }

}
