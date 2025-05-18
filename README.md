# AnonymousChat

AnonymousChat is a secure, real-time messaging application that allows users to communicate anonymously in chat rooms. The application provides end-to-end encryption, message moderation, and user presence tracking.

## Features

- **Secure Authentication**: JWT-based authentication system
- **Anonymous Chat Rooms**: Create and join group chats with anonymous identities
- **Real-time Messaging**: WebSocket-based real-time message delivery
- **End-to-End Encryption**: AES encryption for all messages
- **Content Moderation**: Automatic filtering of inappropriate content
- **User Presence**: Real-time tracking of active users
- **Responsive Design**: Works on desktop and mobile devices

## Technology Stack

- **Backend**: Spring Boot, WebSocket, Spring Security
- **Database**: MongoDB
- **Authentication**: JWT (JSON Web Tokens)
- **Encryption**: AES-256

## Getting Started

### Prerequisites

- Java 17 or higher
- MongoDB 4.4 or higher
- Maven 3.6 or higher

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/oluwasegun5/anonymousChat.git
   ```

2. Navigate to the project directory:
   ```
   cd anonymousChat
   ```

3. Configure MongoDB connection in `application.properties`:
   ```
   spring.data.mongodb.uri=mongodb://localhost:27017/anonymousChat
   ```

4. Build the project:
   ```
   mvn clean install
   ```

5. Run the application:
   ```
   mvn spring-boot:run
   ```

The application will be available at `http://localhost:8080`.

## API Documentation

### Authentication

- **POST /api/auth/register**: Register a new user
- **POST /api/auth/login**: Authenticate and receive JWT token
- **POST /api/auth/refresh-token**: Refresh an expired JWT token

### Chat

- **GET /api/chat/rooms**: Get all chat rooms for the current user
- **POST /api/chat/group**: Create a new group chat
- **GET /api/chat/{roomId}/messages**: Get messages from a specific chat room
- **POST /api/chat/{roomId}/messages**: Send a message to a chat room

### WebSocket Endpoints

- **CONNECT /ws**: Connect to WebSocket server
- **SUBSCRIBE /topic/chat.{roomId}.messages**: Subscribe to messages in a chat room
- **SUBSCRIBE /topic/active-users**: Subscribe to active user updates

## Security

The application implements several security measures:

- JWT authentication for API endpoints
- AES-256 encryption for message content
- Rate limiting to prevent abuse
- Content moderation to filter inappropriate messages

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
