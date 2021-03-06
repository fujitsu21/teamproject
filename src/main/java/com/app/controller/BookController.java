package com.app.controller;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.app.dao.BookDao;
import com.app.model.Book;
import com.app.repository.BookRepository;
import com.app.service.ApiSearch;
import com.app.service.BookService;

@Controller
@RequestMapping("/")
public class BookController {
	/*
	 * repositoryインターフェースを自動インスタンス化
	 * EntityManager自動インスタンス化
	 * Dao自動インスタンス化
	 */
	@Autowired
	private BookRepository bookstore;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private BookDao bookDao;
	@Autowired
	private BookService bookService;
	@PostConstruct
	public void init() {
		bookDao = new BookDao(entityManager);
	}
	
	/*
	 * トップページ
	 */
	@GetMapping("index")
	public String index(Model model) {
		return "index";
	}
	
	@GetMapping("inputform")
	public String output (@RequestParam(name = "number") String number, Model model) {
		// エラーチェック
		if (number == null || number.equals("")) {
			model.addAttribute("errorMessage", "番号を入力してください");
			return index(model);
		}
		// api呼び出し
		ApiSearch apisearch = BookService.service(number);
		
		model.addAttribute("number", ApiSearch.getResult());
		return "searchresult";
	}
	/**
	 * [list] へアクセスがあった場合
	 */
	@RequestMapping("/list")
	public ModelAndView list(ModelAndView mav) {
		// bookstoreテーブルから全件取得
		Iterable<Book> book_list = bookstore.findAll();
		
		// Viewに渡す変数をModelに格納
		mav.addObject("book_list", book_list);
		
		// 画面に出力するViewを指定
		mav.setViewName("list");
		
		// return Model and View
		return mav;
	}
	
	/*
	 * [search]にアクセスがあった場合
	 */
	@RequestMapping("/search")
	public ModelAndView search(HttpServletRequest request, ModelAndView mav) {
		// bookstoreテーブルから検索
		Iterable<Book> book_list = bookDao.find(
				request.getParameter("jan_code"),
				request.getParameter("book_name"),
				request.getParameter("employee_number"),
				request.getParameter("employee_mail")
				);
		// viewに渡す変数をModelに格納
		mav.addObject("book_list", book_list);
		// 画面に出力するViewを指定
		mav.setViewName("orient");
		// ModelとView情報を返す
		return mav;
	}
	
	@RequestMapping("/orient")
	public ModelAndView orient(ModelAndView mav) {
		// bookstoreテーブルから全件取得
		Iterable<Book> book_list = bookstore.findAll();
		
		// Viewに渡す変数をModelに格納
		mav.addObject("book_list", book_list);
		
		// 画面に出力するViewを指定
		mav.setViewName("orient");
		
		// return Model and View
		return mav;
	}
	
	/**
	 * [insert]へアクセスがあった場合
	 */
	@RequestMapping("/insert")
	public ModelAndView insert(@ModelAttribute Book book, ModelAndView mav) {
		// store various for View to Model
		mav.addObject("book", book);
		
		// 画面に出力するViewを指定
		mav.setViewName("insert");
		
		// return Model and View
		return mav;
	}
	
	/*
	 * Deleteメソッド
	 */
	@PostMapping("book/{id}")
	public String destroy(@PathVariable Long id) {
		bookService.delete(id);
		return "redirect:/list";
	}
	
	/**
	 * [insert]へPOST送信された場合
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	// POSTデータをBookインスタンスとして受け取る
	public ModelAndView insertPost(@ModelAttribute @Validated Book book, BindingResult result, ModelAndView mav) {
		
		// if input error exist
		if(result.hasErrors()) {
			// error message
			mav.addObject("message", "入力内容に誤りがあります");
			
			//　画面に出力するViewを指定
			mav.setViewName("insert");
			
			// return Model and View
			return mav;
		}
		
		
		// store to db input data;
		bookstore.saveAndFlush(book);
		
		// redirect
		mav = new ModelAndView("redirect:/list");
		
		// return Model and View
		return mav;
	}
}
