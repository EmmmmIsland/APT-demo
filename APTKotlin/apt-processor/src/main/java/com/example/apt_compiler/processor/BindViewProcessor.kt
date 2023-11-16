package com.example.apt_compiler.processor

import com.example.apt_compiler.annotation.BindView
import com.example.apt_compiler.template.IBindHelper
import com.google.auto.service.AutoService
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.awt.SystemColor.info
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.util.Elements
import javax.lang.model.util.Types


@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.example.apt_compiler.annotation.BindView")
class BindViewProcessor : AbstractProcessor() {
    private var mFiler // 文件管理工具类
            : Filer? = null
    private var mTypesUtils // 类型处理工具类
            : Types? = null
    private var mElement // Element处理工具类
            : Elements? = null
    private val mBindActivity: MutableMap<TypeElement, MutableSet<ViewInfo>> =
        HashMap() //用于记录需要绑定的View的名称和对应的id

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        mFiler = processingEnv.filer
        mTypesUtils = processingEnv.typeUtils
        mElement = processingEnv.elementUtils
    }

    override fun process(set: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (set.isNotEmpty()) {
            val elementsAnnotated = roundEnv?.getElementsAnnotatedWith(BindView::class.java)
            elementsAnnotated?.forEach {
                // VariableElement用于表示字段、枚举常量、方法或构造函数参数、局部变量、资源变量或异常参数。
                val variableElement = it as VariableElement
                // TypeElement表示类或接口程序元素
                val typeElement = variableElement.enclosingElement as TypeElement
                // 将viewInfos实例化
                var viewInfos = mBindActivity[typeElement]
                if (viewInfos == null) {
                    viewInfos = hashSetOf()
                    mBindActivity[typeElement] = viewInfos
                }
                // 拿到注解的值，这里的值是view xml布局的id
                val annotation = variableElement.getAnnotation(BindView::class.java)
                val assemblyId = annotation.value
                viewInfos.add(ViewInfo(variableElement.simpleName.toString(), assemblyId))
            }
            // 生成不同的Activity类，生成路径为...
            mBindActivity.keys.forEach {
                // 获取要绑定的view名称
                val className = it.simpleName.toString()
                // 获取绑定View所在类的包名
                val packageElement = mElement?.getPackageOf(it) as PackageElement
                val packageName = packageElement.qualifiedName.toString()
                // 生成的类的名字
                val generateCalssName = "$className$\$Processor"
                // 开始编写模板代码
                // 编写方法
                val method = MethodSpec.methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(Void.TYPE)
                    // 给inject方法添加Override注解
                    .addAnnotation(Override::class.java)
                    // 给inject方法添加Object类型的target参数
                    .addParameter(Object::class.java, "target")
                mBindActivity[it]?.forEach { viewInfo ->
                    // 循环生成findviewbyId代码
                    method.addStatement("$className obj = ($className)target;")
                        .addStatement("obj.setText(obj.findViewById(${viewInfo.id}));")
                }
                // 编写类
                val generateCalss = TypeSpec.classBuilder(generateCalssName)
                     // 添加对类的修饰
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                     // 为类添加需要实现的接口
                    .addSuperinterface(IBindHelper::class.java)
                     // 为类添加方法
                    .addMethod(method.build())
                    .build()
                // 新增一个文件并写入类
                val javaFile = JavaFile.builder(packageName, generateCalss)
                    .build()
                javaFile.writeTo(mFiler)
            }
            return true
        }
        return false
    }

    //要绑定的View的信息载体
    internal inner class ViewInfo(//view的变量名
        var viewName: String, //xml中的id
        var id: Int
    )
}