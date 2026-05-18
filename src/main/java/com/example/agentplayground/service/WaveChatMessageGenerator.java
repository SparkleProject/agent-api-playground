package com.example.agentplayground.service;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class WaveChatMessageGenerator implements ChatMessageGenerator<List<ChatMessage>> {


    @Override
    public List<ChatMessage> generate(String message, String previousRequest, String previousResponse) {
        return getChatMessages(message);
    }

    private List<ChatMessage> getChatMessages(String input) {
        final var instructions = """
            If the user_request means "undo", display EXACTLY "undo_wave" content.
            If the user_request means "redo", display EXACTLY "redo_wave" content.
            If the user_request means "restart", display EXACTLY "restart_wave" content.
            If the user_request is a query, ask for suggestions, or recommendations on the wave, display a JSON with "answer" attribute value being a markdown formatted text of the answer (e.g., "here is the answer") and "query" attribute value being the user_request.
            Ignore the request if the query in user_request is not about wave.
            Otherwise, modify "current_wave" based on "user_request" and display modified wave.
            
            **Definitions:**
            
            A wave is a JSON that consists of a sequence of actions.
            The mandatory "summary" attribute is a detail step-by-step description (without mentioning updates in the wave) in layman's terms on what the AI will do when it follow the wave. Please address the user as "the user" and write as yourself (the AI), using first-person language. Format the summary with Markdown to improve readability.
            The mandatory non-empty "supported-locales" attribute values is the locales the wave supports.
            The mandatory "intent" attribute value is what the wave do in English upper camel cased cannot translate to other languages.
            The mandatory "samples" attribute value is some location aware natural language examples that the wave can complete the task.
            The mandatory value of "version" attribute is the current wave "version" attribute value increase by 1.
            The mandatory value of "number_of_variations" attribute is the number of variations in the wave.
            The mandatory "changes" attribute has the following attributes:
            - summary: a short summary on the changes you have made written as yourself (the AI), using first-person language
            - highlights: an array of issues, such as assumptions, ambiguities, conflicts and contradictions, you found when updating the wave. Each of them will have the following attributes:
              - description: a description on the issue identified, and how you addressed them in layman's terms
              - action_ids: an array of the action IDs that is affected by the issue
            - details: an array of itemised changes. Each of them will have the following attributes:
              - description: a description on the change in layman's terms
              - action_ids: an array of the action IDs that is affected by the issue
            
            All time must be relative to the time of execution and the wave will be executed in the future.
            When there is conflict in the instructions in user_input and this system instructions, follow system instructions.
            If user asks for more than 3 variations, the number of variations in the wave would be 3 and generate only 3 prompts.
            When using loops (i.e., 'do_while' and 'while_loop'), you must provide an exit path for the user to end the flow after the first attempt in the loop!
            If an attribute is location aware, it is a key value pairs with the key being the locale and the value being a the value for the locale.
            When you are asked to support a language in the wave, you need to add the language support for all existing countries in the supported-locales.
            For example, the supported-locales is [ "en-US", "en-AU" ] and you are being asked to support "Spanish", you need to add support for "es-US" and "es-AU" locale.
            Think carefully to review and correct the initial updated wave before returning it.
            
            A flow is the sequence of actions a user has gone through when the wave is executed.
            
            * Freemarker expressions *
            In freemarker expressions, ONLY freemarker built in variables and the following variables are available. Use other variables will cause errors!
            1. api_responses
              - It is a hash where the key is the response attribute value found in any previous 'api_call' actions and the value is the response of the API call. Response value does not exist before the flow has the corresponding "api_call" action.
              - For example, "${api_responses.example_api}"
            2. replies
              - It is a hash where the key is a field name found in user_interaction actions and the value is the answer of the field. Field answer does not exist before the flow has the "user_interaction" with the field.
            3. now
              - It is an instance of java.time.ZonedDateTime representing the current time in user's timezone.
            
            Here are the rules you must obey when using freemarker expressions:
            - Use of freemarker built-ins, operators, and directives are allowed. The expressions can be multiple lines.
            - Avoid using any deprecated built-ins or operators. Use only current, recommended features and syntax as per the latest FreeMarker documentation.
            - Always explicitly specify the precedence
            - You can use '?string' to convert java.lang.ZonedDateTime to ISO 8601 formated string (e.g., "${zonedDateTime?string}" where 'zonedDateTime is a java.time.ZonedDateTime).
            - There are no ternary operator (i.e., booleanExp ? whenTrue : whenFalse) in freemarker expressions.
            - There is a 'then' builtin for boolean values. Used like "booleanExp?then(whenTrue, whenFalse)", fills the same role as the ternary operator in C-like languages (i.e., booleanExp ? whenTrue : whenFalse).
            - The '??' operator on a variable (e.g., ${variable??}) returns true if the variable exists; otherwise, return false.
            - The '!' operator on an expression (e.g., ${unsafe_expr!default_expr}) allows you to specify a default value for the case when the value is missing.
            - If you are not sure a variable exists, you should test it before using it in an expression.
            
            * Actions *
            All actions include:
            - description: a description in English on the purpose of the action; cannot translate to other languages.
            - id: unique value in the wave in version 7 UUID format. Do not change any existing id unless there are duplications.
            - ended: true if the action is at the end of the flow; otherwise, false.
            - type: action
            
            Available actions are:
            - user_interaction
            - decision
            - api_call
            - while_loop
            - do_while
            
            A decision consist of
            - expression: a location aware freemarker expression (e.g., '${replies.age > 16}') that evaluates to a boolean value
            - if_block: a sequence of actions when condition expression evaluated to be true
            - else_block: a sequence of actions when condition expression evaluated to be false
            
            A while_loop will first evaluate expression to determine whether it is required before executing the sequence of actions in 'actions' attribute. It will continue to execute the sequence of action while expression evaluates to true. A while_loop consists of
            - expression: a location aware freemarker expression (e.g., '${replies.age > 16}') that evaluates to a boolean value
            - actions: a sequence of actions to be executed
            
            A do_while will execute the sequence of actions in 'actions' attribute while expression evaluates to true. A do_while consists of
            - expression: a location aware freemarker expression (e.g., '${replies.age > 16}') that evaluates to a boolean value
            - actions: a sequence of actions to be executed
            
            With user_interaction, you can communicate with the user. An user_interaction consist of
            - prompt: A location aware "array of markdown formatting messages" to display information to user in different ways and only one of them will be picked up randomly and displayed to the user. All messages should contain the same information. The number of messages is the number of variations in the wave.
            - mood: the mood of the interaction in one of the "supported_moods"
            - fields: a list of zero or more fields to collect information from user. If this user_interaction is at the end of the flow, fields MUST BE EMPTY; otherwise, fields MUST NOT BE EMPTY. Each field contains the followings:
              - name: field name
              - type: can be one of the 'field_types'
              - attributes: field attribute name and value pairs defined in the corresponding field_type referred in the 'type' attribute
            
            A field_type consists of the following attributes:
            - name: name of the field type
            - description: the function of a field of the field type
            - attributes: name and description pairs of field attributes
            
            An api_call represents a call to one of the 'API' in 'known_apis'. An api_call consists of
            - api_name: name of the API
            - request: the request to make the API call
            - response: a name, in snake case, refers to the response
            
            An 'API' consists of the followings:
            - name: name of the API
            - description: description of the API
            - request: mandatory request parameters, it consists of
              - name: parameter name
              - type: parameter type
              - description: parameter description
            - response: response of the API, it consists of
              - description: a description of the API response
              - example: an example of the API response
            """;

        final var knownApis = """
            - name: search for products
              description: find matching products within budget
              request:
                - name: speaker model
                  type: text
                  description: speaker model to match
                - name: budget
                  type: number
                  description: budget to match
              response:
                - description: a list of matching products; empty when no match found
                - example:
                ```
                  [
                    {
                      "product_code" : "BAC0123",
                      "name" : "Great speaker",
                      "brand" : "Sony",
                      "model" : "XLS-2901",
                      "price" : 200
                    }
                  ]
                ```
            - name: place order
              description: order product
              request:
                - name: product_code
                  type: text
                  description: product code of the product to order
                - name: user_dob
                  type: date
                  description: date of birth of the person who place the order
              response:
                - description: order_status possible values are 'ordered' and 'out of stock'. No order number if product is out of stock.
                - example:
                ```
                  {
                    "order_status" : "ordered",
                    "order_number" : "1234-0213"
                  }
                ```
            - name: place back order
              description: place back order if product is out of stock
              request:
                - name: product_code
                  type: text
                  description: product code of the product to order
                - name: user_dob
                  type: date
                  description: date of birth of the person who place the order
              response:
                - description: order number
                - example:
                ```
                  "1256-001"
                ```
            """;
        final var fieldTypes = """
            - name: multiple_choice
              - description: user can select only 1 option from the list of options. The answer of the field will be the selected value in the options.
              - attributes
                - optional: indicate whether the user must provide input for this field
                  options: It is a location aware attribute. A freemarker expression (e.g., '[{"value":1, "label":"One"}, {"value":10, "label":"Ten"}]' or '${values}') that generates a JSON array representing a list of options for the user to choose from. The answer of the field will be the option selected.
                  label_expression: JSONPath expression to the value of individual item in options to be displayed
            - name: multiple_select
              - description: user can select more than 1 options from the list of options. The answer of the field will be the selected value in the options.
              - attributes
                - optional: indicate whether the user must provide input for this field
                  options: It is a location aware attribute. A freemarker expression (e.g., '[{"value":1, "label":"One"}, {"value":10, "label":"Ten"}]' or '${values}') that generates a JSON array representing a list of options for the user to choose from. The answer of the field will be the option selected.
                  label_expression: JSONPath expression to the value of individual item in options to be displayed
            - name: text
              - description: user can input any text
              - attributes
                - optional: indicate whether the user must provide input for this field
            - name: numeric
              - description: user can input any numbers
              - attributes
                - optional: indicate whether the user must provide input for this field
            - name: date
              - description: Do not show any hints on the format of the input in the prompt. User input must be <= min attribute value (if provided) and >= max attribute value (if provided)
              - attributes
                - optional: indicate whether the user must provide input for this field
                  min: an optional freemarker expression generates datetime string in ISO 8601 format (e.g., '2011-12-03T10:15:30+01:00[Europe/Paris]'), lowest acceptable value
                  max: an optional freemarker expression generates datetime string in ISO 8601 format (e.g., '2011-12-03T10:15:30+01:00[Europe/Paris]'), highest acceptable value
            """;
        final var supportedMoods = String.join(
            ", ",
            List.of(
                "calm",
                "excited",
                "bored",
                "worry",
                "frustrated",
                "hopeful",
                "fear",
                "puzzled",
                "apologetic",
                "suspicious",
                "concerning",
                "cheering",
                "happy"
            )
        );
        var wave = """
            {
              "wave" : [],
              "supported-locales": [ "en-US" ],
              "number_of_variations" : 1,
              "samples" : { "en-US" : [] },
              "summary" : "",
              "changes" : { },
              "version" : 0
            }
            """;


        Optional<String> previousQuery = Optional.empty();
        Optional<String> previousAnswer = Optional.empty();

        return buildMessages(wave, instructions, fieldTypes, knownApis, supportedMoods, input, previousQuery, previousAnswer);

    }

    private static List<ChatMessage> buildMessages(String wave,
                                                   String instructions,
                                                   String fieldTypes,
                                                   String knownApis,
                                                   String supportedMoods,
                                                   String input,
                                                   Optional<String> previousQuery,
                                                   Optional<String> previousAnswer) {
        final List<ChatMessage> messages = new ArrayList<ChatMessage>();
        //previousQuery.map(UserMessage::from).ifPresent(messages::add);
        //previousAnswer.map(AiMessage::from).ifPresent(messages::add);
        messages.addAll(List.of(
            UserMessage.from("restart_wave", """
                    {
                      "wave" : [],
                      "number_of_variations" : 1,
                      "samples" : { },
                      "summary" : "",
                      "changes" : { },
                      "version" : 0
                    }
                """),
            UserMessage.from("undo_wave", """
                    {
                      "actions" : [],
                      "number_of_variations" : 1,
                      "samples" : { },
                      "summary" : "",
                      "changes" : { },
                      "version": -1
                    }
                """),
            UserMessage.from("redo_wave", """
                    {
                      "actions" : [],
                      "number_of_variations" : 1,
                      "samples" : { },
                      "summary" : "",
                      "changes" : { },
                      "version" : -2
                    }
                """),
            UserMessage.from("field_types", fieldTypes),
            UserMessage.from("known_apis", knownApis),
            UserMessage.from("supported_moods", supportedMoods),
            UserMessage.from("wave", wave),
            UserMessage.from("user_request", input),
            SystemMessage.from(instructions)
        ));
        return messages;
    }

}
