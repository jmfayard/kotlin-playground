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
    val featureFunction = "${featureName}Feature"

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
            createFeatureClass(featureFunction)
        )
        println("Don't forget to register $featureFunction() in Feature.kt")
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

    fun createFeatureClass(featureFunction: String) = """
package com.tignum.backend.features.$featureName

import com.tignum.backend.core.graphql.GraphQLFeatureSetup
import com.tignum.backend.features.Feature
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun ${featureFunction}() = object : Feature {
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

    fun createGraphql(graphqlName: String, isQuery: Boolean) {
        GenerateQueryMutation(this, graphqlName, isQuery)
            .createGraphql()
    }

}

data class GenerateQueryMutation(
    val feature: Feature,
    val graphqlName: String,
    val isQuery: Boolean = true,
) {

    val featureName = feature.featureName
    val useCase = graphqlName.replaceFirstChar { it.uppercase() }
    val unionTest = "${useCase}Test"
    val useCaseProperty = "${graphqlName}UseCase"
    val input = "${useCase}Input"
    val payload = "${useCase}Payload"
    val union = "${useCase}Union"
    val superClass = if (isQuery) "Query" else "Mutation"
    val graphqlClass = "${useCase}$superClass"
    val graphqlTestClass = "${useCase}${superClass}Test"

    fun import(suffix: String) =
        "import com.tignum.backend.features.$featureName.$suffix"

    fun createGraphql() = with(feature) {
        require(camelCaseRegex.matches(graphqlName)) { "graphqlName=$graphqlName is not in camelCase" }
        createDirsIfNecessary()
        val kind = if (isQuery) "Query" else "Mutation"
        println("\n== UseCase ==")
        val PascalCase = graphqlName.replaceFirstChar { it.uppercase() }
        mainDomain.resolve("$PascalCase.kt").logAndWriteText(
            useCase()
        )
        mainGraphql.resolve("$PascalCase${kind}.kt")
            .logAndWriteText(graphqlClass())

        testDomain.resolve("${PascalCase}Test.kt").logAndWriteText(
            useCaseTestClass()
        )

        testGraphql.resolve("$PascalCase${kind}Test.kt").logAndWriteText(
            graphqlTestClass()
        )

        val queryFile = resources.resolve("$graphqlName.graphql")
        queryFile.logAndWriteText(
            graphqlQueryClass()
        )
        println(nextSteps(queryFile))
    }

    fun nextSteps(queryFile: File) =
        """
            |
            |== TODO
            |- [ ] Run  test ➡️ ArchitectureTest ⬅️ to detect remaining issues
            |- [ ] Restart your server
            |- [ ] Try the query ${queryFile}
            |- [ ] Run the tests
            """.trimMargin()


    fun useCase() = """
package com.tignum.backend.features.$featureName.domain

${import("graphql.$input")}
${import("graphql.$payload")}
import com.tignum.backend.core.domain.UseCase
import com.tignum.backend.core.db.transactionIfNeeded
import com.tignum.backend.core.failure.Failure
import com.tignum.backend.core.functional.Either

// TODO
class $useCase() : UseCase<$payload, $input> {
    override suspend fun invoke(params: $input):
        Either<Failure, $payload> = transactionIfNeeded {
        TODO()
    }
}
"""

    fun graphqlClass() = """
package com.tignum.backend.features.$featureName.graphql

${import("domain.$useCase")}
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.$superClass
import com.tignum.backend.core.di.GraphQLDI
import com.tignum.backend.core.graphql.GraphQLPayload
import com.tignum.backend.core.graphql.UnionType
import com.tignum.backend.core.graphql.toGraphQLUnionType
import org.koin.core.component.inject

class $graphqlClass : $superClass, GraphQLDI {
    val $useCaseProperty: $useCase by inject()

    @GraphQLDescription("TODO: description")
    suspend fun $graphqlName(input: $input): $union =
        $useCaseProperty(input).toGraphQLUnionType()
}

interface $union : UnionType

// TODO
data class $payload(
    val name: String,
) : GraphQLPayload, $union

// TODO
data class $input(
    val name: String,
)
""".trimIndent()


    fun useCaseTestClass() = """
package com.tignum.backend.features.$featureName.domain

${import("graphql.$input")}
import com.tignum.backend.UnitTest
import io.kotest.assertions.throwables.shouldNotThrowAny
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class $unionTest : UnitTest() {
    val $useCaseProperty = ${useCase}()

    @Test
    fun failingTest() = runTest {
        shouldNotThrowAny {
            $useCaseProperty($input("TODO"))
        }
    }
}

""".trimIndent()

    fun graphqlTestClass() = """
package com.tignum.backend.features.$featureName.graphql

${import("domain.$useCase")}
import com.tignum.backend.GraphQLTest
import com.tignum.backend.core.domain.returns
import com.tignum.backend.core.matchers.executeQuery
import com.tignum.backend.core.matchers.shouldSucceedAndMatchJsonResource
import org.junit.jupiter.api.Test
import org.koin.test.mock.declareMock

class $graphqlTestClass : GraphQLTest() {
    @Test
    fun ${graphqlName}() {
        declareMock<${useCase}>().returns($payload("TODO"))

        graphQL.executeQuery(queryFile = "$graphqlName/${graphqlName}.graphql")
            .shouldSucceedAndMatchJsonResource("$graphqlName/${graphqlName}.json")
    }
}
"""

    fun graphqlQueryClass(): String {
        val dollar: String = '$'.toString()
        return """
# variable: { "name":  "TODO"}
${superClass.lowercase()} ${useCase}(${dollar}name: String!){
    $graphqlName(input: {name: ${dollar}name}) {
        ...${useCase}Fragment
        ...GraphQLFailureFragment
    }
}

fragment GraphQLFailureFragment on GraphQLFailure {
    __typename
    kind
    httpStatusCode
    message
}

fragment ${useCase}Fragment on ${useCase}Payload {
    __typename
    name
}

""".trimIndent()
    }

}

// end group Feature
