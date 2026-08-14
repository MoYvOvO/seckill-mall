package com.shop.work;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.sql.Types;
import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        // 1. 数据库连接配置（修改成你自己的）
        String url = "jdbc:mysql://localhost:3306/shop";
        String username = "root";
        String password = "123456";

        // 2. 包名配置
        String packageName = "com.shop.work";

        // 3. 输出目录（项目根目录下的 src/main/java）
        String outputDir = System.getProperty("user.dir") + "/src/main/java";

        // 4. XML 文件输出目录
        String xmlOutputDir = System.getProperty("user.dir") + "/src/main/resources/mapper";

        FastAutoGenerator.create(url, username, password)
                // 全局配置
                .globalConfig(builder -> {
                    builder.author("zyw")           // 作者名
                            .outputDir(outputDir)        // 输出目录
                            .disableOpenDir()            // 生成后不自动打开目录
                            .commentDate("yyyy-MM-dd");  // 注释日期格式
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent(packageName)          // 父包名
                            .entity("entity")             // Entity 包名
                            .mapper("mapper")             // Mapper 包名
                            .service("service")           // Service 包名
                            .serviceImpl("service.impl")  // ServiceImpl 包名
                            .controller("controller")     // Controller 包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, xmlOutputDir));
                })
                // 策略配置
                .strategyConfig(builder -> {
                    // 设置要生成的表名（改成你的表名）
                    builder.addInclude("order")
                            .addTablePrefix("")           // 去掉表前缀，如 t_ 可配置
                            // Entity 策略
                            .entityBuilder()
                            .enableLombok()               // 开启 Lombok
                            .enableTableFieldAnnotation() // 开启字段注解
                            .logicDeleteColumnName("deleted")  // 逻辑删除字段
                            // Controller 策略
                            .controllerBuilder()
                            .enableRestStyle()            // 开启 @RestController
                            .enableHyphenStyle()          // 开启驼峰转连字符
                            // Service 策略
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")
                            // Mapper 策略
                            .mapperBuilder()
                            .enableBaseResultMap()        // 生成 BaseResultMap
                            .enableBaseColumnList();      // 生成 BaseColumnList
                             builder.entityBuilder().enableFileOverride();
                             builder.mapperBuilder().enableFileOverride();
                             builder.serviceBuilder().enableFileOverride();
                             builder.controllerBuilder().enableFileOverride();
                })
                // 使用 Freemarker 模板引擎
                .templateEngine(new FreemarkerTemplateEngine())
                // 执行生成
                .execute();

        System.out.println("代码生成完成！");
    }
}