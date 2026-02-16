package com.example.plugin.ui;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import com.google.gson.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * 内联JSON编辑器组件
 */
public class InlineJsonEditor extends JBPanel<InlineJsonEditor> {
    
    private final Project project;
    private final Editor editor;
    private final Document document;
    private String jsonValue;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public InlineJsonEditor(Project project, String initialValue) {
        this(project, initialValue, new Dimension(400, 120));
    }
    
    public InlineJsonEditor(Project project, String initialValue, Dimension preferredSize) {
        super(new BorderLayout());
        this.project = project;
        this.jsonValue = formatJson(initialValue != null ? initialValue : "{}");
        
        // 创建文档和编辑器
        EditorFactory editorFactory = EditorFactory.getInstance();
        document = editorFactory.createDocument(this.jsonValue);
        editor = editorFactory.createEditor(document, project, JsonFileType.INSTANCE, false);
        
        setupEditor(preferredSize);
        setupPanel();
        
        // 添加焦点监听器来自动格式化
        editor.getContentComponent().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                formatCurrentContent();
            }
        });
    }
    
    private void setupEditor(Dimension preferredSize) {
        if (editor instanceof EditorEx) {
            EditorEx editorEx = (EditorEx) editor;
            
            // 设置编辑器属性
            EditorSettings settings = editorEx.getSettings();
            settings.setLineNumbersShown(false);
            settings.setLineMarkerAreaShown(false);
            settings.setFoldingOutlineShown(false);
            settings.setRightMarginShown(false);
            settings.setWhitespacesShown(false);
            settings.setIndentGuidesShown(true);
            settings.setCaretRowShown(true);
            
            // 设置语法高亮
            editorEx.setHighlighter(EditorHighlighterFactory.getInstance()
                .createEditorHighlighter(project, JsonFileType.INSTANCE));
            
            // 设置颜色方案
            EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
            editorEx.setColorsScheme(scheme);
            
            // 设置边框
            editorEx.setBorder(JBUI.Borders.customLine(Color.GRAY));
        }
        
        // 设置首选大小
        editor.getComponent().setPreferredSize(preferredSize);
        editor.getComponent().setMinimumSize(new Dimension(200, 60));
    }
    
    private void setupPanel() {
        add(editor.getComponent(), BorderLayout.CENTER);
        
        // 添加工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        toolBar.setOpaque(false);
        
        JButton formatButton = new JButton("Format");
        formatButton.setFont(formatButton.getFont().deriveFont(11f));
        formatButton.addActionListener(e -> formatCurrentContent());
        
        JButton validateButton = new JButton("Validate");
        validateButton.setFont(validateButton.getFont().deriveFont(11f));
        validateButton.addActionListener(e -> validateJson());
        
        toolBar.add(formatButton);
        toolBar.add(validateButton);
        
        add(toolBar, BorderLayout.SOUTH);
    }
    
    private String formatJson(String json) {
        try {
            if (json == null || json.trim().isEmpty()) {
                return "{}";
            }
            JsonElement element = JsonParser.parseString(json);
            return gson.toJson(element);
        } catch (JsonSyntaxException e) {
            return json; // 返回原始内容如果格式化失败
        }
    }
    
    private void formatCurrentContent() {
        String currentText = document.getText();
        String formatted = formatJson(currentText);
        if (!formatted.equals(currentText)) {
            SwingUtilities.invokeLater(() -> {
                document.setText(formatted);
                jsonValue = formatted;
            });
        }
    }
    
    private void validateJson() {
        try {
            JsonParser.parseString(document.getText());
            showMessage("JSON is valid!", false);
        } catch (JsonSyntaxException e) {
            showMessage("Invalid JSON: " + e.getMessage(), true);
        }
    }
    
    private void showMessage(String message, boolean isError) {
        JOptionPane.showMessageDialog(
            this,
            message,
            isError ? "JSON Error" : "JSON Validation",
            isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    public String getJsonValue() {
        return document.getText();
    }
    
    public void setJsonValue(String json) {
        String formatted = formatJson(json);
        SwingUtilities.invokeLater(() -> {
            document.setText(formatted);
            jsonValue = formatted;
        });
    }
    
    public boolean isValidJson() {
        try {
            JsonParser.parseString(document.getText());
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }
    
    public void disposeEditor() {
        if (editor != null && !editor.isDisposed()) {
            EditorFactory.getInstance().releaseEditor(editor);
        }
        super.removeAll();
    }
    
    // 创建一个紧凑版本的编辑器，用于表格中
    public static InlineJsonEditor createCompact(Project project, String initialValue) {
        return new InlineJsonEditor(project, initialValue, new Dimension(300, 80));
    }
    
    // 创建一个大版本的编辑器，用于对话框中
    public static InlineJsonEditor createLarge(Project project, String initialValue) {
        return new InlineJsonEditor(project, initialValue, new Dimension(500, 200));
    }
}