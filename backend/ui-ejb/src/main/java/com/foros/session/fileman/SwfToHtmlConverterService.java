package com.foros.session.fileman;

import javax.ejb.Local;
import java.util.List;

@Local
public interface SwfToHtmlConverterService {
    List<String> convert(
            FileManager fileManager,
            String dir,
            String swfFileName,
            Boolean withoutClickUrlMacro,
            String htmlWithoutClickUrlMacroFileName,
            Boolean withClickUrlMacro,
            String htmlWithClickUrlMacroFileName,
            String clickMacro
    );
}
