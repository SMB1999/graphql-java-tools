package com.coxautodev.graphql.tools

import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.CompletableFuture

/**
 * @author Andrew Potter
 */
class SchemaClassScannerSpec extends Specification {

    def "scanner handles futures and immediate return types"() {
        when:
            SchemaParser.newParser()
                .resolvers(new FutureImmediateQuery())
                .schemaString("""
                    type Query {
                        future: Int!
                        immediate: Int!
                    }
                """)
                .build()
        then:
            noExceptionThrown()
    }

    private class FutureImmediateQuery implements GraphQLQueryResolver {
        CompletableFuture<Integer> future() {
            CompletableFuture.completedFuture(1)
        }

        Integer immediate() {
            1
        }
    }

    def "scanner handles primitive and boxed return types"() {
        when:
            SchemaParser.newParser()
                .resolvers(new PrimitiveBoxedQuery())
                .schemaString("""
                type Query {
                    primitive: Int!
                    boxed: Int!
                }
            """)
                .build()
        then:
            noExceptionThrown()
    }

    private class PrimitiveBoxedQuery implements GraphQLQueryResolver {
        int primitive() {
            1
        }

        Integer boxed() {
            1
        }
    }

    def "scanner handles different scalars with same java class"() {
        when:
            SchemaParser.newParser()
                .resolvers(new ScalarDuplicateQuery())
                .schemaString("""
                type Query {
                    string: String!
                    id: ID!
                }
            """)
                .build()

        then:
            noExceptionThrown()
    }

    private class ScalarDuplicateQuery implements GraphQLQueryResolver {
        String string() { "" }
        String id() { "" }
    }
}
