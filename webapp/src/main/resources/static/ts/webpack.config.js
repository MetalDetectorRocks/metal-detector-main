const path = require("path");

module.exports = {
    mode: "development",
    entry: {
        search: "./src/bundles/search.ts",
        myartists: "./src/bundles/my-artists.ts",
        releases: "./src/bundles/releases.ts",
        homepage: "./src/bundles/homepage.ts",
        profile: "./src/bundles/profile.ts"
    },
    watch: true,
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
