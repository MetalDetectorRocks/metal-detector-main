const path = require("path");
const CleanPlugin = require("clean-webpack-plugin");

module.exports = {
    mode: "production",
    entry: {
        blog: "./src/blog/app.ts",
        search: "./src/search/app.ts",
        myartists: "./src/my-artists/app.ts"
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
