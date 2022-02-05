const path = require("path");

module.exports = {
    mode: "development",
    entry: {
        search: "./src/bundles/search.ts",
        my_artists: "./src/bundles/my-artists.ts",
        releases: "./src/bundles/releases.ts",
        dashboard: "./src/bundles/dashboard.ts",
        spotify_synchronization: "./src/bundles/spotify-synchronization.ts",
        account_details: "./src/bundles/account-details.ts",
        notification_settings: "./src/bundles/notification-settings.ts",
        home: "./src/bundles/home.ts",
    },
    watchOptions: {
        aggregateTimeout: 200,
        poll: 1000,
        ignored: "node_modules/**",
    },
    output: {
        filename: "[name].bundle.js",
        path: path.resolve(__dirname, "dist"),
        publicPath: "dist", // relative to index.html
    },
    devtool: "inline-source-map",
    module: {
        rules: [
            {
                test: /\.ts$/,
                use: "ts-loader",
                exclude: /node_modules/,
            },
            {
                test: /\.css$/i,
                use: ["style-loader", "css-loader"],
            },
        ],
    },
    resolve: {
        extensions: [".ts", ".js"],
    },
};
