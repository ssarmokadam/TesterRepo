package com.devonfw.devonlocale.translator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
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

  private boolean sibling = false;

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

    Map<String, String> siblingStringMap = new HashMap<>();
    Node node, siblingNode;
    Map<String, Node> childMap, newChildMap;
    Set<String> keySet = root.keySet();
    System.out.println(keySet);
    for (String key : keySet) {
      node = root.get(key);

      if (node.getText() == null && node.getChildren().size() > 1) {
        childMap = node.getChildren();
        Set<String> siblingNodeSet = childMap.keySet();
        this.startJsonStringBuilder.append("\"" + key + "\": {").append(Constant.NEW_LINE_CHAR);
        this.endJsonStringBuilder.append("}");
        int i = 0;
        int j = 0;
        for (String siblingName : siblingNodeSet) {
          siblingNode = childMap.get(siblingName);
          if (siblingNode.getText() == null && siblingNode.getChildren().size() == 1) {
            newChildMap = siblingNode.getChildren();
            this.startJsonStringBuilder.append("\"" + siblingName + "\": {").append(Constant.NEW_LINE_CHAR);
            // this.endJsonStringBuilder.append("}");
            this.sibling = true;
            StringBuilder tempBuilder = createJsonString(newChildMap);
            System.out.println("tempBuilder " + tempBuilder.toString());
            this.sibling = false;
            // this.startJsonStringBuilder.append(tempBuilder.toString());
            if (j < siblingNodeSet.size() - 1) {
              this.startJsonStringBuilder.append(",");
            }
            j++;
          } else {
            if (i != 0) {
              this.startJsonStringBuilder.append(",");
            }
            this.startJsonStringBuilder
                .append("\"" + siblingName + "\" : " + "\"" + childMap.get(siblingName).getText() + "\"");
            i++;
          }

        } // for close

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

        if (this.sibling) {
          this.startJsonStringBuilder.append("\"" + key + "\" : " + "\"" + node.getText() + "\"" + "}");
        } else {
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

    }

    return this.completeJsonString;
  }

}
