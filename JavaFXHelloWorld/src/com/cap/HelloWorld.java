package com.cap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * TODO ssarmoka This type ...
 *
 * @author ssarmoka
 */
public class HelloWorld extends Application {

  private Reflections reflections = new Reflections(ClasspathHelper.forPackage("com.cap"), new SubTypesScanner(),
      new TypeAnnotationsScanner(), new MethodAnnotationsScanner());

  public static void main(String[] args) {

    HelloWorld hello = new HelloWorld();
    Method[] methods = hello.getClass().getMethods();
    for (Method method : methods) {
      hello.addInputType(method);
    }

    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {

    primaryStage.setTitle("Hello World!");
    Button btn = new Button();
    btn.setText("Say 'Hello World'");
    btn.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {

        System.out.println("Hello World!");
      }
    });

    StackPane root = new StackPane();
    root.getChildren().add(btn);
    primaryStage.setScene(new Scene(root, 300, 250));
    primaryStage.show();
  }

  @InputTypes(types = { @InputType(name = "generic"), @InputType(name = "path") })
  public void tryAnnote() {

    Set<Class<?>> annotatedClasses = this.reflections.getTypesAnnotatedWith(InputTypes.class);

  }

  private void addInputType(Method method) {

    // Set<Class<?>> annotatedClasses = this.reflections.getTypesAnnotatedWith(InputTypes.class);
    // System.out.println(annotatedClasses.size());
    // Object[] a = annotatedClasses.toArray();
    //
    // for (int i = 0; i < a.length; i++) {
    // System.out.println(a.toString());
    // Class<?> currentClass = a.getClass();
    // System.out.println(currentClass.getName());
    // }
    Annotation annotations = method.getAnnotation(InputTypes.class);
    if (annotations != null) {
      InputTypes types = (InputTypes) annotations;
      List<InputType> typeList = Arrays.asList(types.types());
      for (InputType type : typeList) {
        System.out.println("type is " + type.name());
      }
    }
  }
}
