# API Versioning POC

This project is a Proof of Concept (POC) demonstrating how to implement API versioning in a JAX-RS application without duplicating controller logic for every version.

## Overview

The core idea is to intercept incoming requests containing version information (e.g., `/v1/resource`), extract the version, rewrite the path to a canonical form (e.g., `/resource`), and then route the request to the appropriate service implementation using a dynamic proxy.

## Architecture

### Key Components

1.  **`VersionExtractionFilter`** (`code.apiversion.core.VersionExtractionFilter`)
    - **Role**: Interceptor.
    - **Function**: It looks for a version pattern (e.g., `/v{number}/`) in the request URI.
    - **Action**:
        - Extracts the version number.
        - Stores it in the `RequestVersionContext`.
        - Rewrites the request URI to remove the version prefix so the JAX-RS container can match it to the controller.

2.  **`RequestVersionContext`** (`code.apiversion.core.RequestVersionContext`)
    - **Role**: Context Holder.
    - **Function**: Uses `ThreadLocal` to store the API version for the duration of the request.
    - **Usage**: Accessible anywhere in the request thread to check the requested version.

3.  **`VersionRoutingHandler`** (`code.apiversion.core.VersionRoutingHandler`)
    - **Role**: Dynamic Router.
    - **Function**: A `java.lang.reflect.InvocationHandler` that acts as a proxy for service implementations.
    - **Logic**:
        - Checks `RequestVersionContext.getVersion()`.
        - Routes the method call to the implementation registered for that version.
        - **Fallback**: If the requested version is not found, it attempts to fall back to a lower version (e.g., requesting v3 might fall back to v2 if v3 doesn't exist).

## Usage

### Structure
Requests should be formatted as:
`GET /example/v{version}/{resource}`

Example:
- `GET /example/v1/greeting` -> Routes to V1 implementation.
- `GET /example/v2/greeting` -> Routes to V2 implementation.
- `GET /example/greeting` -> Defaults to V1.

### Testing

Run the included unit and integration tests to see the logic in action:

```bash
mvnw test
```

## Setup

1.  Clone the repository.
2.  Build the project:
    ```bash
    mvnw clean install
    ```
3.  Run the tests as shown above.
