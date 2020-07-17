class BlogService {

    constructor() {
    }

    printGreeting(): void {
        console.log("Hello From Blog Service!");
    }
}

const blogService = new BlogService();
blogService.printGreeting();
