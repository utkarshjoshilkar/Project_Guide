import tree_sitter_java as tsjava
from tree_sitter import Language, Parser

JAVA_LANGUAGE = Language(tsjava.language())
parser = Parser(JAVA_LANGUAGE)

source_code = """
package com.example.service;

import java.util.List;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProjectService {

    public void createProject() {
        validateProject();
        saveProject();
    }

    private void validateProject() {
    }

    private void saveProject() {
    }
}
"""

tree = parser.parse(bytes(source_code, "utf8"))
root_node = tree.root_node

classes = []
methods = []
imports = []

def traverse(node):
    if node.type == 'class_declaration':
        for child in node.children:
            if child.type == 'identifier':
                classes.append(child.text.decode('utf8'))
                break
    elif node.type == 'method_declaration':
        for child in node.children:
            if child.type == 'identifier':
                methods.append(child.text.decode('utf8'))
                break
    elif node.type == 'import_declaration':
        import_path = []
        for child in node.children:
            if child.type not in ('import', 'static', ';'):
                import_path.append(child.text.decode('utf8'))
        if import_path:
            imports.append("".join(import_path).strip())

    for child in node.children:
        traverse(child)

traverse(root_node)

print("Classes:", classes)
print("Methods:", methods)
print("Imports:", imports)
