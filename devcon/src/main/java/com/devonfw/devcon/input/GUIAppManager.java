package com.devonfw.devcon.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.impl.CommandRegistryImpl;
import com.devonfw.devcon.common.utils.Utils;
import com.google.common.base.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * TODO ssarmoka This type ...
 *
 * @author ssarmoka
 */
public class GUIAppManager extends Application {

  public static void main(String[] args) {

    launch(args);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start(Stage primaryStage) throws Exception {

    String image = GUIAppManager.class.getResource("Logo_Devcon-background.jpg").toExternalForm();
    // StackPane root = new StackPane();

    // TODO Auto-generated method stub
    primaryStage.setTitle("Devcon");

    BorderPane borderPane = new BorderPane();
    borderPane.setTop(getMenus());

    borderPane.setStyle("-fx-background-image: url('" + image + "'); " + "-fx-background-position: center center; "
        + "-fx-background-repeat: stretch;");
    Scene scene = new Scene(borderPane, 889, 600);
    // scene.getStylesheets().add("Trial.css");
    // String css = GUIAppManager.class.getResource("Trial.css").toExternalForm();
    // scene.setFill(Color.rgb(98, 71, 55));
    primaryStage.setScene(scene);

    primaryStage.show();
  }

  private MenuBar getMenus() {

    Utils utils = new Utils();

    final MenuBar menuBar = new MenuBar();
    CommandRegistry registry = new CommandRegistryImpl("com.devonfw.devcon.modules.*");
    List<CommandModuleInfo> modules = utils.sortModules(registry.getCommandModules());
    List<Menu> menuList = new ArrayList<>();
    for (int i = 0; i < modules.size(); i++) {
      final Menu menu = new Menu(modules.get(i).getName());
      Optional<CommandModuleInfo> commands = registry.getCommandModule(modules.get(i).getName());
      Collection<Command> sortedCommands = utils.sortCommands(commands.get().getCommands());
      Iterator<Command> itrCommands = sortedCommands.iterator();
      while (itrCommands.hasNext()) {
        Command cmd = itrCommands.next();
        MenuItem item = new MenuItem(cmd.getName());
        menu.getItems().add(item);
        item.setOnAction(new MenuEventHandler());
      }
      if (i == 0) {
        MenuItem item = new MenuItem("Exit");
        menu.getItems().add(item);
        item.setOnAction(new MenuEventHandler());
      }
      menuBar.getMenus().add(menu);
    }
    return menuBar;
  }

}
