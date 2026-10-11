import tree_sitter_java as tsjava
from tree_sitter import Language, Parser
from app.dto.CodeAnalysisRequest import CodeAnalysisRequest
from app.dto.CodeAnalysisResponse import CodeAnalysisResponse

class CodeAnalysisService:
    def __init__(self):
        self.JAVA_LANGUAGE = Language(tsjava.language())
        self.parser = Parser(self.JAVA_LANGUAGE)

    def analyze_java(self, request: CodeAnalysisRequest) -> CodeAnalysisResponse:
        try:
            tree = self.parser.parse(bytes(request.sourceCode, "utf8"))
            root_node = tree.root_node

            classes = []
            methods = []
            imports = []

            def traverse(node):
                if node.type in ('class_declaration', 'interface_declaration', 'record_declaration', 'enum_declaration'):
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

            return CodeAnalysisResponse(
                filePath=request.filePath,
                language="JAVA",
                classes=classes,
                methods=methods,
                imports=imports
            )
        except Exception as e:
            # Raise generic exception to be handled by controller safely
            raise Exception(f"Failed to parse Java file: {str(e)}")
