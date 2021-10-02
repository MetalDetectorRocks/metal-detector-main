const path = require("path");
const webpack = require("webpack");
const CleanPlugin = require("clean-webpack-plugin");

module.exports = {
    mode: "production",
    entry: {
        search: "./src/bundles/search.ts",
        my_artists: "./src/bundles/my-artists.ts",
        releases: "./src/bundles/releases.ts",
        homepage: "./src/bundles/homepage.ts",
        spotify_synchronization: "./src/bundles/spotify-synchronization.ts",
        account_details: "./src/bundles/account-details.ts",
        notification_settings: "./src/bundles/notification-settings.ts",
    },
    output: {
        filename: "[name].bundle.js",
        path: path.resolve(__dirname, "dist"),
    },
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
    plugins: [
        // cleans the output folder before writing to it
        new CleanPlugin.CleanWebpackPlugin(),
    ],
};
