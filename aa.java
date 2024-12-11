import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownEditor extends JFrame {
    private JTextArea editor;
    private JEditorPane preview;
    private JButton boldButton, italicButton, headerButton, spoilerButton, copyButton;

    public MarkdownEditor() {
        setTitle("Markdown Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // エディタエリア (JTextArea)
        editor = new JTextArea();
        editor.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editor.setLineWrap(true);
        editor.setWrapStyleWord(true);

        // プレビューエリア (JEditorPane)
        preview = new JEditorPane();
        preview.setEditable(false);
        preview.setContentType("text/html");
        preview.setText("<html><body></body></html>");

        // ボタンを作成
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

        boldButton = new JButton("太字");
        italicButton = new JButton("斜体");
        headerButton = new JButton("見出し1");
        spoilerButton = new JButton("ネタバレ");
        copyButton = new JButton("コピー");

        // ボタンにアクションリスナーを追加
        boldButton.addActionListener(e -> insertMarkdown("**", "**"));
        italicButton.addActionListener(e -> insertMarkdown("*", "*"));
        headerButton.addActionListener(e -> insertMarkdown("# ", ""));
        spoilerButton.addActionListener(e -> insertMarkdown("||", "||"));
        copyButton.addActionListener(e -> copyToClipboard());

        toolbar.add(boldButton);
        toolbar.add(italicButton);
        toolbar.add(headerButton);
        toolbar.add(spoilerButton);
        toolbar.add(copyButton);

        // エディタとプレビューをスクロールペインに設定
        JScrollPane editorScrollPane = new JScrollPane(editor);
        JScrollPane previewScrollPane = new JScrollPane(preview);

        // レイアウトに追加
        add(toolbar, BorderLayout.NORTH);
        add(editorScrollPane, BorderLayout.CENTER);
        add(previewScrollPane, BorderLayout.SOUTH);

        // エディタの内容が変更されたらプレビューを更新
        editor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updatePreview();
            }
        });
    }

    // Markdown記法をエディタに挿入
    private void insertMarkdown(String before, String after) {
        int start = editor.getSelectionStart();
        int end = editor.getSelectionEnd();
        String selectedText = editor.getSelectedText();

        if (selectedText != null) {
            editor.replaceRange(before + selectedText + after, start, end);
        } else {
            editor.insert(before + after, start);
        }
    }

    // プレビューを更新する
    private void updatePreview() {
        String markdownText = editor.getText();
        String htmlContent = markdownToHtml(markdownText);
        preview.setText(htmlContent);
    }

    // MarkdownをHTMLに変換
    private String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return "<html><body>" + renderer.render(document) + "</body></html>";
    }

    // コピー機能
    private void copyToClipboard() {
        String text = editor.getText();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(text), null);
        JOptionPane.showMessageDialog(this, "Markdownがコピーされました！");
    }

    // メインメソッド
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MarkdownEditor().setVisible(true);
        });
    }
}
