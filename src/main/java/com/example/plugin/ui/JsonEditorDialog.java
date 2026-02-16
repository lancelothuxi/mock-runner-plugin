package com.example.plugin.ui;

import com.google.gson.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * JSON可视化编辑器对话框
 */
public class JsonEditorDialog extends DialogWrapper {
    
    private final Project project;
    private String jsonValue;
    private JBTextArea textArea;
    private JTree jsonTree;
    private DefaultTreeModel treeModel;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public JsonEditorDialog(Project project, String initialValue) {
        super(project);
        this.project = project;
        this.jsonValue = initialValue != null ? initialValue : "{}";
        setTitle("JSON Mock Data Editor");
        setSize(800, 600);
        init();
    }
    
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 创建分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        
        // 左侧：树形视图
        JPanel treePanel = createTreePanel();
        splitPane.setLeftComponent(treePanel);
        
        // 右侧：文本编辑器
        JPanel textPanel = createTextPanel();
        splitPane.setRightComponent(textPanel);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // 底部按钮
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 初始化数据
        updateTreeFromJson();
        
        return mainPanel;
    }
    
    private JPanel createTreePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(JBUI.Borders.customLine(Color.GRAY));
        
        JLabel label = new JLabel("Visual Editor");
        label.setBorder(JBUI.Borders.empty(5));
        panel.add(label, BorderLayout.NORTH);
        
        // 创建树
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("JSON");
        treeModel = new DefaultTreeModel(root);
        jsonTree = new JTree(treeModel);
        jsonTree.setRootVisible(true);
        jsonTree.setShowsRootHandles(true);
        
        JBScrollPane treeScrollPane = new JBScrollPane(jsonTree);
        panel.add(treeScrollPane, BorderLayout.CENTER);
        
        // 树操作按钮
        JPanel treeButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Field");
        JButton removeButton = new JButton("Remove");
        JButton editButton = new JButton("Edit Value");
        
        addButton.addActionListener(e -> addField());
        removeButton.addActionListener(e -> removeField());
        editButton.addActionListener(e -> editValue());
        
        treeButtonPanel.add(addButton);
        treeButtonPanel.add(removeButton);
        treeButtonPanel.add(editButton);
        
        panel.add(treeButtonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTextPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(JBUI.Borders.customLine(Color.GRAY));
        
        JLabel label = new JLabel("JSON Text");
        label.setBorder(JBUI.Borders.empty(5));
        panel.add(label, BorderLayout.NORTH);
        
        textArea = new JBTextArea(jsonValue);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setLineWrap(false);
        
        JBScrollPane scrollPane = new JBScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton formatButton = new JButton("Format JSON");
        JButton validateButton = new JButton("Validate");
        JButton syncFromTextButton = new JButton("Sync from Text");
        JButton syncToTextButton = new JButton("Sync to Text");
        
        formatButton.addActionListener(e -> formatJson());
        validateButton.addActionListener(e -> validateJson());
        syncFromTextButton.addActionListener(e -> updateTreeFromJson());
        syncToTextButton.addActionListener(e -> updateJsonFromTree());
        
        panel.add(formatButton);
        panel.add(validateButton);
        panel.add(syncFromTextButton);
        panel.add(syncToTextButton);
        
        return panel;
    }
    
    private void addField() {
        TreePath selectedPath = jsonTree.getSelectionPath();
        if (selectedPath == null) {
            selectedPath = new TreePath(treeModel.getRoot());
        }
        
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
        
        String fieldName = JOptionPane.showInputDialog(this.getContentPanel(), "Field Name:", "Add Field", JOptionPane.QUESTION_MESSAGE);
        if (fieldName != null && !fieldName.trim().isEmpty()) {
            String fieldValue = JOptionPane.showInputDialog(this.getContentPanel(), "Field Value:", "Add Field", JOptionPane.QUESTION_MESSAGE);
            if (fieldValue == null) fieldValue = "";
            
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(fieldName + ": " + fieldValue);
            selectedNode.add(newNode);
            treeModel.reload();
            
            // 展开到新节点
            TreePath newPath = new TreePath(newNode.getPath());
            jsonTree.expandPath(newPath);
            jsonTree.setSelectionPath(newPath);
        }
    }
    
    private void removeField() {
        TreePath selectedPath = jsonTree.getSelectionPath();
        if (selectedPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            if (selectedNode != treeModel.getRoot()) {
                treeModel.removeNodeFromParent(selectedNode);
            }
        }
    }
    
    private void editValue() {
        TreePath selectedPath = jsonTree.getSelectionPath();
        if (selectedPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            String currentValue = selectedNode.toString();
            
            String newValue = JOptionPane.showInputDialog(this.getContentPanel(), "Edit Value:", currentValue);
            if (newValue != null) {
                selectedNode.setUserObject(newValue);
                treeModel.reload();
            }
        }
    }
    
    private void formatJson() {
        try {
            JsonElement element = JsonParser.parseString(textArea.getText());
            String formatted = gson.toJson(element);
            textArea.setText(formatted);
        } catch (JsonSyntaxException e) {
            JOptionPane.showMessageDialog(this.getContentPanel(), "Invalid JSON: " + e.getMessage(), "JSON Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void validateJson() {
        try {
            JsonParser.parseString(textArea.getText());
            JOptionPane.showMessageDialog(this.getContentPanel(), "JSON is valid!", "Validation", JOptionPane.INFORMATION_MESSAGE);
        } catch (JsonSyntaxException e) {
            JOptionPane.showMessageDialog(this.getContentPanel(), "Invalid JSON: " + e.getMessage(), "JSON Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTreeFromJson() {
        try {
            JsonElement element = JsonParser.parseString(textArea.getText());
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("JSON");
            buildTreeFromJson(root, element);
            treeModel.setRoot(root);
            treeModel.reload();
            
            // 展开根节点
            jsonTree.expandPath(new TreePath(root));
        } catch (JsonSyntaxException e) {
            JOptionPane.showMessageDialog(this.getContentPanel(), "Invalid JSON: " + e.getMessage(), "JSON Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buildTreeFromJson(DefaultMutableTreeNode parent, JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            for (String key : obj.keySet()) {
                JsonElement value = obj.get(key);
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + ": " + getValuePreview(value));
                parent.add(node);
                
                if (value.isJsonObject() || value.isJsonArray()) {
                    buildTreeFromJson(node, value);
                }
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                JsonElement value = array.get(i);
                DefaultMutableTreeNode node = new DefaultMutableTreeNode("[" + i + "]: " + getValuePreview(value));
                parent.add(node);
                
                if (value.isJsonObject() || value.isJsonArray()) {
                    buildTreeFromJson(node, value);
                }
            }
        }
    }
    
    private String getValuePreview(JsonElement element) {
        if (element.isJsonPrimitive()) {
            return element.getAsString();
        } else if (element.isJsonObject()) {
            return "{...}";
        } else if (element.isJsonArray()) {
            return "[...]";
        } else if (element.isJsonNull()) {
            return "null";
        }
        return element.toString();
    }
    
    private void updateJsonFromTree() {
        // 这是一个简化版本，实际实现会更复杂
        JOptionPane.showMessageDialog(this.getContentPanel(), "Tree to JSON sync is not fully implemented yet. Please edit JSON text directly.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public String getJsonValue() {
        return textArea.getText();
    }
    
    @Override
    protected void doOKAction() {
        // 验证JSON
        try {
            JsonParser.parseString(textArea.getText());
            jsonValue = textArea.getText();
            super.doOKAction();
        } catch (JsonSyntaxException e) {
            JOptionPane.showMessageDialog(this.getContentPanel(), "Invalid JSON: " + e.getMessage(), "JSON Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}