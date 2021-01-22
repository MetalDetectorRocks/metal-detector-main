const path = require("path");
const webpack = require('webpack');

module.exports = {
    mode: "development",
    entry: {
        search: "./src/bundles/search.ts",
        my_artists: "./src/bundles/my-artists.ts",
        releases: "./src/bundles/releases.ts",
        homepage: "./src/bundles/homepage.ts",
        spotify_synchronization: "./src/bundles/spotify-synchronization.ts",
        account_details: "./src/bundles/account-details.ts"
    },
    watchOptions: {
        aggregateTimeout: 200,
        poll: 1000,
        ignored: 'node_modules/**'
    },
    output: {
        filename: "[name].bundle.js",
        path: path.resolve(__dirname, "dist"),
        publicPath: "dist" // relative to index.html
    },
    devtool: "inline-source-map",
    module: {
        rules: [
            {
                test: /\.ts$/,
                use: "ts-loader",
                exclude: /node_modules/
            }
        ]
    },
    resolve: {
        extensions: [".ts", ".js"]
    }
};
