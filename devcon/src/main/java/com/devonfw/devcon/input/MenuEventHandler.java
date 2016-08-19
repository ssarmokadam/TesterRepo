package com.devonfw.devcon.input;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

/**
 * TODO ssarmoka This type ...
 *
 * @author ssarmoka
 */
public class MenuEventHandler implements EventHandler<ActionEvent> {

  /**
   * {@inheritDoc}
   */
  @Override
  public void handle(ActionEvent event) {

    MenuItem item = ((MenuItem) event.getSource());
    String menu = item.getParentMenu().getText();
    String menuItem = item.getText().toLowerCase();
    if (menuItem.equalsIgnoreCase("Exit")) {
      System.exit(0);
    }
    switch (menu) {
    case "oasp4j":
      switch (menuItem) {
      case "build":
        System.out.println("Event occured on Menu " + menuItem + " menu is " + menu);
      }

    }
  }

}
