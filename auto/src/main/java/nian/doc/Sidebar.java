package nian.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Sidebar {

    static String format = " * [%s](files/%s?t=%s)\r\n";
    static String noparentformat = "* [%s](files/%s?t=%s)\r\n";
    static Map<String, Menu> levelMap = new HashMap<>(8);

    static {
        //一级目录
        //前端是默认一级目录默认的页面，与是目录名
        //对应文件名前缀，"i-"格式，如无序号，都归到0
        int i = 0;  //排序，将序号写在文件前，即可以归到同一类
        levelMap.put("1".toLowerCase(), new Menu("* 工具指南\n", i++));
        levelMap.put("about".toLowerCase(), new Menu("* 关于\n", i++));
    }

    public static void main(String[] args) throws Exception {
        String path = Sidebar.class.getClassLoader().getResource("").getPath();
        String rootPath = path.substring(0,path.indexOf("/docs"));
        String fileDir = rootPath+"/docs/files";
        File dir = new File(fileDir);
        File[] files = dir.listFiles();
        Stream<File> filesStream = Stream.of(files);
        Map<String, List<FileExt>> menuMap = filesStream
                .sorted(Comparator.comparing(File::getName))
                .map(file -> {
                    if (file.isDirectory()) {
                        return null;
                    }
                    FileExt fileExt = new FileExt();
                    String fileName = file.getName();
                    fileExt.menu= "";
                    for (String k:levelMap.keySet()){
                        if (fileName.contains(k)){
                            fileExt.menu = k;
                        }
                    }
                   if (fileExt.getMenu().equals("") && fileName.toLowerCase().indexOf("-")>-1){
                      String menuName = fileName.toLowerCase().substring(0, fileName.toLowerCase().indexOf("-"));
                      if (!levelMap.containsKey(menuName.toLowerCase())){
                          levelMap.put(menuName.toLowerCase(),new Menu("* "+ menuName +"\n", levelMap.size()+1));
                       }
                       fileExt.menu = menuName;
                    }
                   fileExt.file = file;
                   return fileExt;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(FileExt::getMenu));

        StringBuilder output = new StringBuilder();
        output.append("* [首页](/?t=" + System.currentTimeMillis() + ")\n");
        for (Map.Entry<String, List<FileExt>> entry : menuMap.entrySet()) {
            if (levelMap.containsKey(entry.getKey())){
                Menu menu = levelMap.get(entry.getKey().toLowerCase());
                output.append(menu.parentName);
            }else{
//                for (String k:levelMap.keySet()){
//                    if (filename.contains(k)){
//                        output.append(k);
//                    }
//                }
            }
            for (FileExt fileExt : entry.getValue()) {
                String filename = fileExt.file.getName();
                String title = filename.substring(filename.indexOf("-") + 1, filename.length() - 3);

                String line = "";
                if (fileExt.getMenu()==null || fileExt.getMenu().equals("")){
                    line = String.format(noparentformat, title, filename, System.currentTimeMillis());
                }else {
                    line = String.format(format, title, filename, System.currentTimeMillis());
                }
                output.append(line);
            }
        }
        System.out.println(output);
        String sidebarFilepath = rootPath+"/docs/_sidebar.md";
       // FileOutputStream out = new FileOutputStream(new File(sidebarFilepath));
       // out.write(output.toString().getBytes());
       // out.close();

        OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(new File(sidebarFilepath)), "utf-8");
        oStreamWriter.append(output.toString());
        oStreamWriter.close();

        Thread.sleep(10);
    }

    static class Menu {
        String parentName;
        int order;

        public Menu(String parentName, int order) {
            this.parentName = parentName;
            this.order = order;
        }
    }

    static class FileExt {
        File file;
        String menu;

        public File getFile() {
            return file;
        }

        public String getMenu() {
            return menu;
        }
    }
}