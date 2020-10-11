const path = require("path");
const CleanPlugin = require("clean-webpack-plugin");

module.exports = {
    mode: "production",
    entry: {
        search: "./src/bundles/search.ts",
        myartists: "./src/bundles/my-artists.ts",
        releases: "./src/bundles/releases.ts",
        homepage: "./src/bundles/homepage.ts",
        profile: "./src/bundles/profile.ts"
    },
    output: {
        filename: "[name].bundle.js",
        path: path.resolve(__dirname, "dist"),
    },
    devtool: "none",
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
    },
    plugins: [
        // cleans the output folder before writing to it
        new CleanPlugin.CleanWebpackPlugin()
    ]
};
