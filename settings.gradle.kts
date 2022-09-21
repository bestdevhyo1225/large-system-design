rootProject.name = "large-scale-system"

include(
    // order-payment-server
    "order-payment-server",
    "order-payment-server:api",
    "order-payment-server:common",
    "order-payment-server:domain",
    "order-payment-server:infrastructure:jpa",
    // shortened-url-server
    "shortened-url-server",
    // shortened-url-server-ver2
    "shortened-url-server-ver2:command",
    "shortened-url-server-ver2:common",
    "shortened-url-server-ver2:domain:rdbms",
    "shortened-url-server-ver2:domain:nosql",
    "shortened-url-server-ver2:query",
    // shortened-url-server-ver3
    "shortened-url-server-ver3:command",
    "shortened-url-server-ver3:common",
    "shortened-url-server-ver3:domain",
    "shortened-url-server-ver3:infrastructure:jpa",
    "shortened-url-server-ver3:infrastructure:r2dbc",
    "shortened-url-server-ver3:infrastructure:redis",
    "shortened-url-server-ver3:infrastructure:redis-reactive",
    "shortened-url-server-ver3:infrastructure:redisson",
    "shortened-url-server-ver3:query",
    "shortened-url-server-ver3:query-webflux",
    // sns-service
    "sns-service:application:admin",
    "sns-service:application:batch",
    "sns-service:application:command",
    "sns-service:application:query",
    "sns-service:application:query-webflux",
    "sns-service:common",
    "sns-service:data:jpa",
    "sns-service:data:redis",
    "sns-service:domain",
    "sns-service:infrastructure:aws",
    "sns-service:infrastructure:okhttp3",
    // sns-feed-service
    "sns-feed-service:application:api:command",
    "sns-feed-service:application:api:query",
    "sns-feed-service:application:event-worker:feed-server",
    "sns-feed-service:common",
    "sns-feed-service:data:jpa",
    "sns-feed-service:data:redis:feed",
    "sns-feed-service:data:redis:post",
    "sns-feed-service:domain",
    "sns-feed-service:infrastructure:aws",
    "sns-feed-service:infrastructure:kafka",
    "sns-feed-service:infrastructure:okhttp3",
)
