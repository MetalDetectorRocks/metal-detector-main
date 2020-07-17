const path = require("path");

module.exports = {
    mode: "development",
    entry: {
        blog: "./src/blog/app.ts",
        search: "./src/search/app.ts",
        myartists: "./src/my-artists/app.ts"
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
