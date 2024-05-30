package es.in2.desmos.domain.utils;

public class MessageUtils {

    private MessageUtils() {
        throw new IllegalStateException("Utility class");
    }
    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource not found";
    public static final String UNAUTHORIZED_ACCESS_MESSAGE = "Unauthorized access";
    public static final String ACCESS_FORBIDDEN_MESSAGE = "Access forbidden";
    public static final String ENTITY_ALREADY_EXIST_MESSAGE = "Entity already exist";
    public static final String ERROR_DURING_REQUEST_MESSAGE = "Error during request: {}";
    public static final String BROKER_URL_VALUE_MESSAGE ="Broker URL: {}";
    public static final String RESOURCE_CREATED_MESSAGE = "Resource created successfully.";
    public static final String ERROR_CREATING_RESOURCE_MESSAGE = "Error while creating resource: {}";
    public static final String RESOURCE_RETRIEVED_MESSAGE = "Resource retrieved successfully.";
    public static final String ERROR_RETRIEVING_RESOURCE_MESSAGE = "Error while retrieving resource: {}";
    public static final String RESOURCE_UPDATED_MESSAGE = "Resource updated successfully.";
    public static final String ERROR_UPDATING_RESOURCE_MESSAGE = "Error while updating resource: {}";
    public static final String RESOURCE_DELETED_MESSAGE = "Resource deleted successfully.";
    public static final String ERROR_DELETING_RESOURCE_MESSAGE = "Error while deleting resource: {}";
    public static final String ENTITY_ID_NOT_FOUND_ERROR_MESSAGE = "Entity ID field not found.";
    public static final String READING_JSON_ENTITY_ERROR_MESSAGE = "Error while reading entity JSON: {}";
    public static final String SUBSCRIPTION_OBJECT_CREATED_MESSAGE = "Subscription object created successfully.";
    public static final String ERROR_CREATING_SUBSCRIPTION_OBJECT_MESSAGE = "Error while creating subscription object: {}";
    public static final String SUBSCRIPTION_CREATED_MESSAGE = "Subscription created successfully.";
    public static final String ERROR_CREATING_SUBSCRIPTION_MESSAGE = "Error while creating subscription: {}";
    public static final String SUBSCRIPTION_RETRIEVED_MESSAGE = "Subscription retrieved successfully.";
    public static final String ERROR_RETRIEVING_SUBSCRIPTION_MESSAGE = "Error while retrieving subscription: {}";
    public static final String SUBSCRIPTION_UPDATED_MESSAGE = "Subscription updated successfully.";
    public static final String ERROR_UPDATING_SUBSCRIPTION_MESSAGE = "Error while updating subscription: {}";
    public static final String SUBSCRIPTIONS_FETCHED_SUCCESSFULLY_MESSAGE = "Subscription fetched successfully.";
    public static final String ERROR_FETCHING_SUBSCRIPTIONS_MESSAGE = "Error while fetching subscriptions: {}";
    public static final String ERROR_PARSING_SUBSCRIPTION_TO_JSON_MESSAGE = "Error parsing subscription to JSON: {}";
    public static final String ERROR_PARSING_SUBSCRIPTIONS_MESSAGE = "Error parsing subscription to JSON.";
    public static final String RESPONSE_CODE_200 = "200";
    public static final String RESPONSE_CODE_201 = "201";
    public static final String RESPONSE_CODE_204 = "204";
    public static final String RESPONSE_CODE_400 = "400";
    public static final String RESPONSE_CODE_401 = "401";
    public static final String RESPONSE_CODE_403 = "403";
    public static final String RESPONSE_CODE_404 = "404";
    public static final String RESPONSE_CODE_500 = "500";
    public static final String RESPONSE_CODE_200_DESCRIPTION = "Entity retrieved successfully";
    public static final String RESPONSE_CODE_201_DESCRIPTION = "Entity created successfully";
    public static final String RESPONSE_CODE_204_DESCRIPTION = "Entity updated/delete successfully";
    public static final String RESPONSE_CODE_400_DESCRIPTION = "Bad request";
    public static final String RESPONSE_CODE_401_DESCRIPTION = "Unauthorized";
    public static final String RESPONSE_CODE_403_DESCRIPTION = "Forbidden";
    public static final String RESPONSE_CODE_404_DESCRIPTION = "Entity not found";
    public static final String RESPONSE_CODE_500_DESCRIPTION = "Internal server error";

}
