package x.add

import java.io.File

fun main() {
    println(scanFeatures())
    //Feature("deleteme").createFeature()
    Feature("deleteme").createGraphql("someCat", isQuery = false)
}

// start group Feature
fun xFolder(path: String) = File("/Users/jmfayard/tignum/backend-tignum-x/$path")

fun scanFeatures(): List<String> {
    val dir = xFolder("src/main/kotlin/com/tignum/backend/features/")
    return dir.listFiles()
        .filter { it.isDirectory }
        .map { it.name }
}

data class Feature(val featureName: String) {
    val camelCaseRegex = "[a-z][a-zA-Z0-9]+".toRegex()
    val repo = xFolder(".").canonicalFile
    val main = xFolder("src/main/kotlin/com/tignum/backend/features/$featureName")
    val mainData = main.resolve("data")
    val mainDomain = main.resolve("domain")
    val mainGraphql = main.resolve("graphql")
    val test = xFolder("src/test/kotlin/com/tignum/backend/features/$featureName")
    val testData = test.resolve("data")
    val testGraphql = test.resolve("graphql")
    val testDomain = test.resolve("domain")
    val resources = xFolder("src/main/resources/$featureName")
    fun allDirs(): List<File> =
        listOf(main, mainData, mainGraphql, mainDomain, test, testDomain, testData, testGraphql, testDomain, resources)

    val File.relative: String
        get() = relativeTo(repo).path

    fun File.logAndWriteText(content: String) = apply {
        println("creating ${relativeTo(repo).path}")
        writeText(content)
    }


    fun createFeature() {
        createDirsIfNecessary()
        val PascalCase = featureName.replaceFirstChar { it.uppercase() }
        main.resolve("${PascalCase}Feature.kt").logAndWriteText(
            createFeatureClass(featureName)
        )
        println("Don't forget to register ${featureName}Feature() in Feature.kt")
    }

    fun createGraphql(graphqlName: String, isQuery: Boolean) {
        require(camelCaseRegex.matches(graphqlName)) { "graphqlName=$graphqlName is not in camelCase" }
        createDirsIfNecessary()
        val kind = if (isQuery) "Query" else "Mutation"
        println("\n== UseCase ==")
        val PascalCase = graphqlName.replaceFirstChar { it.uppercase() }
        mainDomain.resolve("$PascalCase.kt").logAndWriteText(
            useCase(featureName, PascalCase)
        )
        mainGraphql.resolve("$PascalCase${kind}.kt").logAndWriteText(
            graphqlClass(featureName, PascalCase, graphqlName, kind)
        )

        testDomain.resolve("${PascalCase}Test.kt").logAndWriteText(
            useCaseTestClass(featureName, PascalCase, graphqlName)
        )

        testGraphql.resolve("$PascalCase${kind}Test.kt").logAndWriteText(
            graphqlTestClass(featureName, PascalCase, graphqlName, kind)
        )

        val queryFile = resources.resolve("$graphqlName.graphql")
        queryFile.logAndWriteText(
            graphqlQueryClass(graphqlName, PascalCase, kind)
        )
        println("")
        println("== TODO")
        println("- [ ] Run  test ➡️ ArchitectureTest ⬅️ to detect remaining issues")
        println("- [ ] Restart your server")
        println("- [ ] Try the query ${queryFile.relative}")
        println("- [ ] Run the tests")
    }


    fun createDirsIfNecessary() {
        println("checking directories for feature $featureName in $repo")
        allDirs().forEach { folder ->
            if (folder.exists().not()) {
                println("Creating folder ${folder.relative}")
                folder.mkdir()
            }
        }
    }

}

fun createFeatureClass(featureName: String) = """
package com.tignum.backend.features.$featureName

import com.tignum.backend.core.graphql.GraphQLFeatureSetup
import com.tignum.backend.features.Feature
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun ${featureName}Feature() = object : Feature {
    override fun name(): String = "$featureName"

    override fun diModule(): Module = module {
        // singleOf(::SomeClass)
    }

    override fun graphQLSetup() = GraphQLFeatureSetup(
        supportedPackages = supportedPackages(),
        queries = listOf(),
        mutations = listOf(),
    )
}

""".trimIndent()

fun useCase(featureName: String, name: String) = """
package com.tignum.backend.features.$featureName.domain

import com.tignum.backend.core.domain.UseCase
import com.tignum.backend.features.$featureName.graphql.${name}Input
import com.tignum.backend.features.$featureName.graphql.${name}Payload
import com.tignum.backend.core.db.transactionIfNeeded
import com.tignum.backend.core.failure.Failure
import com.tignum.backend.core.functional.Either

// TODO
class $name() : UseCase<${name}Payload, ${name}Input> {
    override suspend fun invoke(params: ${name}Input):
        Either<Failure, ${name}Payload> = transactionIfNeeded {
        TODO()
    }
}
"""

fun graphqlClass(featureName: String, PascalCase: String, camelCase: String, kind: String) = """
package com.tignum.backend.features.${featureName}.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.${kind}
import com.tignum.backend.core.di.GraphQLDI
import com.tignum.backend.core.graphql.GraphQLPayload
import com.tignum.backend.core.graphql.UnionType
import com.tignum.backend.core.graphql.toGraphQLUnionType
import com.tignum.backend.features.${featureName}.domain.${PascalCase}
import org.koin.core.component.inject

class ${PascalCase}${kind} : ${kind}, GraphQLDI {
    val ${camelCase}UseCase: ${PascalCase} by inject()

    @GraphQLDescription("TODO: description")
    suspend fun $camelCase(input: ${PascalCase}Input): ${PascalCase}Union =
        ${camelCase}UseCase(input).toGraphQLUnionType()
}

interface ${PascalCase}Union : UnionType

// TODO
data class ${PascalCase}Payload(
    val name: String,
) : GraphQLPayload, ${PascalCase}Union

// TODO
data class ${PascalCase}Input(
    val name: String,
)
""".trimIndent()


fun useCaseTestClass(featureName: String, PascalCase: String, camelCase: String) = """
package com.tignum.backend.features.$featureName.domain

import com.tignum.backend.UnitTest
import com.tignum.backend.features.$featureName.graphql.${PascalCase}Input
import io.kotest.assertions.throwables.shouldNotThrowAny
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class ${PascalCase}Test : UnitTest() {
    val ${camelCase} = ${PascalCase}()

    @Test
    fun failingTest() = runTest {
        shouldNotThrowAny {
            ${camelCase}(${PascalCase}Input("TODO"))
        }
    }
}

""".trimIndent()

fun graphqlTestClass(featureName: String, PascalCase: String, camelCase: String, kind: String) = """
package com.tignum.backend.features.${featureName}.graphql

import com.tignum.backend.GraphQLTest
import com.tignum.backend.core.domain.returns
import com.tignum.backend.core.matchers.executeQuery
import com.tignum.backend.core.matchers.shouldSucceedAndMatchJsonResource
import com.tignum.backend.features.$featureName.domain.${PascalCase}
import org.junit.jupiter.api.Test
import org.koin.test.mock.declareMock

class ${PascalCase}${kind}Test : GraphQLTest() {
    @Test
    fun ${camelCase}() {
        declareMock<${PascalCase}>().returns(${PascalCase}Payload("TODO"))

        graphQL.executeQuery(queryFile = "${featureName}/${camelCase}.graphql")
            .shouldSucceedAndMatchJsonResource("${featureName}/${camelCase}.json")
    }
}
"""

fun graphqlQueryClass(graphqlName: String, PascalCase: String, kind: String): String {
    val dollar: String = '$'.toString()
    return """
# variable: { "name":  "TODO"}
${kind.lowercase()} ${PascalCase}(${dollar}name: String!){
    $graphqlName(input: {name: ${dollar}name}) {
        ...${PascalCase}Fragment
        ...GraphQLFailureFragment
    }
}

fragment GraphQLFailureFragment on GraphQLFailure {
    __typename
    kind
    httpStatusCode
    message
}

fragment ${PascalCase}Fragment on ${PascalCase}Payload {
    __typename
    name
}

""".trimIndent()
}

// end group Feature
