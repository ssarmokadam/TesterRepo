package com.devonfw.devcon.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.api.data.DevconOption;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.DistributionType;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.exception.InvalidConfigurationStateException;
import com.devonfw.devcon.common.impl.DistributionInfoImpl;
import com.devonfw.devcon.common.impl.ProjectInfoImpl;
import com.devonfw.devcon.common.impl.utils.DistributionFolderProcessor;
import com.devonfw.devcon.common.impl.utils.ProjectFolderProcessor;
import com.devonfw.devcon.common.impl.utils.SenchaWorkspaceFolderProcessor;
import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Optional;

/**
 * This class is to be used as a service/singleton, to obtain the Distribution and ProjectInfo types from paths on the
 * file system
 *
 * @author ivanderk
 */
public class ContextPathInfo {

  private static final String OASP_IDE = "oasp-ide";

  private static final String DEVON_DIST = "devon-dist";

  private static final String TYPE = "type";

  private static final String VERSION = "version";

  private static final String CONF_SETTINGS_JSON = "conf/settings.json";

  private static final String DEVON_JSON = "devon.json";

  private static final String COMBINED = "combined";

  private static final String DEVON4SENCHA = "devon4sencha";

  private static final String OASP4JS = "oasp4js";

  private static final String OASP4J = "oasp4j";

  /**
   *
   * @param path get {@link Path} from Strng
   * @return
   */
  private Path getPath(String path) {

    return FileSystems.getDefault().getPath(path);
  }

  /**
   * @TODO to see whether Commons has a better implementation?
   * @return CWD - Current working directory as a Path instance
   */
  public Path getCurrentWorkingDirectory() {

    File file = new File(".");
    return getPath(file.getAbsolutePath());
  }

  /**
   *
   * @return User´s HOME directory
   */
  public Path getHomeDirectory() {

    return getPath(FileUtils.getUserDirectoryPath());

  }

  /**
   *
   * @return Distribution Info if CWD within a Devon Distrubution or OASP IDE
   */
  public Optional<DistributionInfo> getDistributionRoot() {

    return getDistributionRoot(getCurrentWorkingDirectory());
  }

  /**
   *
   * @param currentDir pass directory as String
   * @return Distribution Info if currentDir within a Devon Distrubution or OASP IDE
   */
  public Optional<DistributionInfo> getDistributionRoot(String path) {

    return getDistributionRoot(getPath(path));
  }

  /**
   *
   * @param aPath pass directory as Path instance
   * @return Distribution Info if currentDir within a Devon Distrubution or OASP IDE
   */
  public Optional<DistributionInfo> getDistributionRoot(Path aPath) {

    DistributionFolderProcessor interceptor = new DistributionFolderProcessor();

    try {
      TreeClimber.climb(aPath, interceptor);
      if (interceptor.isFound()) {

        DistributionInfo info = getDistributionInfo(interceptor.getFoundPath());
        return Optional.of(info);
      } else {
        return Optional.absent();
      }
    } catch (InvalidConfigurationStateException err) {
      throw err;
    } catch (Exception err) {
      throw new InvalidConfigurationStateException(err);
    }
  }

  public DistributionInfo getDistributionInfo(Path distPath) throws FileNotFoundException, IOException, ParseException {

    Path settingsPath = distPath.resolve(CONF_SETTINGS_JSON);
    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new FileReader(settingsPath.toFile()));

    JSONObject json = (JSONObject) obj;

    // PATCH to fix mistaken settings.json as distributed with Devonfw 2.0
    String sversion = json.get(VERSION).toString();
    if (sversion.equals("2.0")) {
      sversion = "2.0.0";
    }
    Version version = Version.valueOf(sversion);

    DistributionType distType;
    String disttype = json.get(TYPE).toString();
    if (disttype.toLowerCase().equals(DEVON_DIST)) {
      distType = DistributionType.DevonDist;
    } else if (disttype.toLowerCase().equals(OASP_IDE)) {
      distType = DistributionType.OASPIDE;
    } else {
      throw new InvalidConfigurationStateException("type property does not contain either 'devon-dist' nor 'oasp-ide'");
    }

    return new DistributionInfoImpl(distPath, distType, version);
  }

  public Optional<ProjectInfo> getProjectRoot() {

    return getProjectRoot(getCurrentWorkingDirectory());
  }

  public Optional<ProjectInfo> getProjectRoot(String dir) {

    if ((dir == null) || (dir.isEmpty())) {
      return this.getProjectRoot();
    } else {
      return this.getProjectRoot(getPath(dir));
    }
  }

  public Optional<ProjectInfo> getProjectRoot(Path currentDir) {

    ProjectFolderProcessor interceptor = new ProjectFolderProcessor();

    try {
      TreeClimber.climb(currentDir, interceptor);
      if (interceptor.isFound()) {

        ProjectInfo info = getProjectInfo(interceptor.getFoundPath());
        return Optional.of(info);
      } else {
        return Optional.absent();
      }
    } catch (InvalidConfigurationStateException err) {
      throw err;
    } catch (Exception err) {
      throw new InvalidConfigurationStateException(err);
    }

  }

  public ProjectInfo getProjectInfo(Path projectPath) throws FileNotFoundException, IOException, ParseException {

    Path settingsPath = projectPath.resolve(DEVON_JSON);
    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new FileReader(settingsPath.toFile()));

    JSONObject config = (JSONObject) obj;
    Version version = Version.valueOf(config.get(VERSION).toString());
    List<ProjectInfo> projects = new ArrayList<>();
    ProjectType projectType;

    String projtype = config.get(TYPE).toString();
    if (projtype.toLowerCase().equals(COMBINED)) {
      projectType = ProjectType.COMBINED;

      JSONArray subJson = (JSONArray) config.get("projects");
      for (Object e : subJson) {
        Path resolved = projectPath.resolve(e.toString());
        projects.add(getProjectInfo(resolved));
      }

    } else if (projtype.toLowerCase().equals(OASP4J)) {
      projectType = ProjectType.OASP4J;
    } else if (projtype.toLowerCase().equals(OASP4JS)) {
      projectType = ProjectType.OASP4JS;
    } else if (projtype.toLowerCase().equals(DEVON4SENCHA)) {
      projectType = ProjectType.DEVON4SENCHA;
    } else {
      throw new InvalidConfigurationStateException(
          "type property does not contain valid ProjectInfoType: 'combined', 'oasp4j', 'oasp4js' or 'devon4sencha' ");
    }

    /**
     * HERE WE COULD ADD DYNAMIC PROPS
     */

    return new ProjectInfoImpl(projectPath, projectType, version, config, projects);
  }

  /**
   * @param projectPath
   * @return
   */
  public Optional<ProjectInfo> getCombinedProjectRoot(Path projectPath) {

    Optional<ProjectInfo> projectInfo = getProjectRoot(projectPath);
    if (!projectInfo.isPresent() || projectInfo.get().getProjecType().equals(ProjectType.COMBINED)) {
      return projectInfo;
    } else {
      return getProjectRoot(projectPath.getParent());
    }
  }

  public Optional<ProjectInfo> getCombinedProjectRoot(String dir) {

    if ((dir == null) || (dir.isEmpty())) {
      return this.getCombinedProjectRoot();
    } else {
      return this.getCombinedProjectRoot(getPath(dir));
    }
  }

  public Optional<ProjectInfo> getCombinedProjectRoot() {

    return this.getCombinedProjectRoot(getCurrentWorkingDirectory());
  }

  public Optional<Path> getSenchaWorkspaceRoot(Path path) {

    SenchaWorkspaceFolderProcessor interceptor = new SenchaWorkspaceFolderProcessor();

    TreeClimber.climb(path, interceptor);
    if (interceptor.isFound()) {

      return Optional.of(interceptor.getFoundPath());
    } else {
      return Optional.absent();
    }
  }

  public Optional<Path> getSenchaWorkspaceRoot(String dir) {

    if ((dir == null) || (dir.isEmpty())) {
      return this.getSenchaWorkspaceRoot();
    } else {
      return this.getSenchaWorkspaceRoot(getPath(dir));
    }
  }

  public Optional<Path> getSenchaWorkspaceRoot() {

    return this.getSenchaWorkspaceRoot(getCurrentWorkingDirectory());
  }

  public List<DevconOption> getGlobalOptions() {

    List<DevconOption> globalOptions = new ArrayList<DevconOption>();

    String root = (Devcon.IN_EXEC_JAR) ? "resources/" : "";
    URL globalParamsFileURL = ClassLoader.getSystemClassLoader().getResource(root + Constants.GLOBAL_PARAMS_FILE);

    try {
      return getGlobalOptionsFromResource(globalParamsFileURL);
    } catch (IOException | ParseException e) {
      // TODO Auto-generated catch block
      throw new InvalidConfigurationStateException(e);
    }
  }

  private List<DevconOption> getGlobalOptionsFromResource(URL resourceURL)
      throws FileNotFoundException, IOException, ParseException {

    JSONParser parser = new JSONParser();
    List<DevconOption> globalOptions = new ArrayList<>();

    Object obj = parser.parse(IOUtils.toString(resourceURL, "utf-8"));
    JSONArray json = (JSONArray) obj;

    Iterator<Object> it = json.iterator();

    while (it.hasNext()) {
      try {
        JSONObject j = (JSONObject) it.next();

        String opt = j.get("opt") != null ? j.get("opt").toString() : " ";
        String longOpt = j.get("longOpt") != null ? j.get("longOpt").toString() : " ";
        String description = j.get("description") != null ? j.get("description").toString() : " ";

        globalOptions.add(new DevconOption(opt, longOpt, description));

      } catch (Exception e) {
        // TODO implement logs
        System.out.println("Error reading a global option. Please check the global options file.");
      }

    }

    return globalOptions;
  }

}
