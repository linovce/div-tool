package com.linovce.div.xmind.map;

import org.xmind.core.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @ClassName: file.FileNode
 * @Description: 文件操作
 * @version: v1.0.0
 * @author: linovce
 * @date: 2020/4/29 9:33
 */
public class DirectoryOperation {

    // 创建思维导图的工作空间
    static IWorkbookBuilder workbookBuilder = Core.getWorkbookBuilder();
    static IWorkbook workbook = workbookBuilder.createWorkbook();

    // 获得默认sheet
    static ISheet primarySheet = workbook.getPrimarySheet();

    // 获得对应目录下的所有文件的文件名
    public ArrayList<String> getFiles(String path){
        ArrayList<String> files = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for(int i=0;i<tempList.length;i++){
            if(tempList[i].isFile()){
                files.add(tempList[i].toString());
            }
        }

        return files;
    }

    // 获得对应目录下的所有文件夹的文件夹名
    public ArrayList<String> getFolder(String path){
        ArrayList<String> directorys = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for(int i=0;i<tempList.length;i++){
            if(tempList[i].isDirectory()){
                directorys.add(tempList[i].toString());
            }
        }

        return directorys;
    }

    // 获得对应目录下的所有文件(夹)的信息
    public FileNode getAll(String path){
        FileNode root = new FileNode(path);
        ArrayList<FileNode> subfiles = new ArrayList<>();
        ArrayList<String> folders = getFolder(path);
        ArrayList<String> files = getFiles(path);

        for(String tempPath : folders){
            if(tempPath.endsWith(".svn"))
                continue;
            FileNode folder = getAll(tempPath);
            subfiles.add(folder);
        }

        for(String tempFile : files){
            FileNode file = new FileNode(tempFile);
            subfiles.add(file);
        }
        root.setSubFiles(subfiles);

        return root;
    }

    public ITopic buildFileTree(FileNode root) throws IOException, CoreException {

        int index = root.getFileName().lastIndexOf("\\");
        String name = root.getFileName().substring(index+1);
        ITopic topic = workbook.createTopic();
        topic.setTitleText(name);

        if(root.getSubFiles()==null){
            return topic;
        }

        for(FileNode file : root.getSubFiles()){
            ITopic chapterTopic = buildFileTree(file);
            topic.add(chapterTopic,ITopic.ATTACHED);
        }

        return topic;
    }

    /**
     * @Function: com.linovce.div.xmind.map.DirectoryOperation::run
     * @Description: 启动方法，生成思维导图
     * @version: v1.0.0
     * @author: linovce
     * @date: 2021/3/30 14:03
     */
    public void run(String getPath,String savePath) throws IOException, CoreException{
        DirectoryOperation directoryOperation = new DirectoryOperation();
        FileNode root = directoryOperation.getAll(getPath);
        ITopic rootTopic = directoryOperation.buildFileTree(root);
        primarySheet.replaceRootTopic(rootTopic);
        // 保存
        workbook.save(savePath);
    }
}