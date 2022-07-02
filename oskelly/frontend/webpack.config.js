const path = require('path');
const webpack = require('webpack');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin');
var AssetsPlugin = require('assets-webpack-plugin');


module.exports = {
    entry: './src/main.js',
    output: {
        path: path.resolve(__dirname, '../src/main/resources/static/styles/'),
        publicPath: '/styles/',
        filename: "webpack-main.js"
    },
    module: {
        loaders: [
            {
                test: /\.(css|sass|scss)$/,
                loader: ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: [
                        "css-loader",
                        'postcss-loader',
                        "sass-loader",
                    ]
                })
            },
            {
                test: /\.(png|woff|woff2|eot|ttf|svg|otf)$/,
                loader: 'url-loader',
                options: {
                    limit: 1000,
                    name: "assets/[hash].[ext]"
                }
            }
        ]
    },
    plugins: [
        new ExtractTextPlugin('main.[contenthash].css'),
        new OptimizeCssAssetsPlugin({
            cssProcessorOptions: { discardComments: { removeAll: true } }
        }),
        new AssetsPlugin({ filename: 'webpack_assets.json',
            path: path.resolve(__dirname, '../src/main/resources/static/')})
    ]
};